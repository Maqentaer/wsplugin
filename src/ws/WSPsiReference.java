package ws;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.util.Function;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.index.WSFileBasedIndexExtension;
import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WSPsiReference implements PsiReference, PsiPolyVariantReference {
    protected PsiElement element;
    protected TextRange textRange;
    protected Project project;
    protected String value;
    protected VirtualFile appDir;

    public WSPsiReference(String value, PsiElement element, TextRange textRange, Project project, VirtualFile appDir) {
        this.element = element;
        this.textRange = textRange;
        this.project = project;
        this.value = value;
        this.appDir = appDir;
    }

    @Override
    public String toString() {
        return getCanonicalText();
    }

    public PsiElement getElement() {
        return this.element;
    }

    public TextRange getRangeInElement() {
        return textRange;
    }

    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        throw new IncorrectOperationException();
    }

    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        throw new IncorrectOperationException();
    }

    public boolean isReferenceTo(PsiElement element) {
        return resolve() == element;
    }

    @NotNull
    public Object[] getVariants() {
        return new Object[0];
    }

    public boolean isSoft() {
        return false;
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return value;
    }

    @Nullable
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }


    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
        String controlName = "";
        String controlPrefix = "";
        String path = "";

        Pattern pattern = Pattern.compile("SBIS3\\.(\\w+)\\.(\\w+)((/\\w+)+\\.js)?");
        Matcher matcher = pattern.matcher(value);

        if (matcher.find()) {
            controlPrefix = matcher.group(1);
            controlName = matcher.group(2);
            path = matcher.group(3);
        }

        String control = (controlPrefix != null) ? controlPrefix + "." + controlName : "";
        Collection<VirtualFile> files = WSFileBasedIndexExtension.getFileByComponentName(project, control);

        if (files.size() == 0) {
            return new ResolveResult[0];
        }

        final PsiManager instance = PsiManager.getInstance(project);
        final String finalPath = path;

        Collection<VirtualFile> resultFilesCollection = new HashSet<VirtualFile>();

        if (finalPath != null && !finalPath.isEmpty()) {
            for(VirtualFile file: files){
                VirtualFile pathFile = file.getParent().findFileByRelativePath(finalPath);
                if(pathFile != null){
                    resultFilesCollection.add(pathFile);
                }
            }
        } else {
            resultFilesCollection = files;
        }

        return PsiElementResolveResult.createResults(ContainerUtil.map(resultFilesCollection, new Function<VirtualFile, PsiElement>() {
            @Override
            public PsiElement fun(VirtualFile file) {
                try {
                        return instance.findFile(file);
                } catch (Exception ignore) {}
                return null;
            }
        }));
    }
}
