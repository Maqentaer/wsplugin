package ws;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScopesCore;
import com.intellij.util.CommonProcessors;
import com.intellij.util.Function;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.indexing.FileBasedIndex;
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
        final GlobalSearchScope scope = GlobalSearchScope.projectScope(project);
        final CommonProcessors.CollectProcessor<String> processor = new CommonProcessors.CollectProcessor<String>();
        FileBasedIndex.getInstance().processAllKeys(WSFileBasedIndexExtension.WS_PATH_INDEX, processor, scope, null);

        return processor.toArray(new String[processor.getResults().size()]);
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
        String path = "";

        Pattern pattern = Pattern.compile("(SBIS3\\.\\w+\\.\\w+)(.*)?");
        Matcher matcher = pattern.matcher(value);

        if (matcher.find()) {
            controlName = matcher.group(1);
            path = matcher.group(2);
        }

        if (controlName == null || controlName.isEmpty()) {
            return new ResolveResult[0];
        }

        Collection<VirtualFile> files = WSFileBasedIndexExtension.getFileByComponentName(project, controlName);

        if (files.size() == 0) {
            return new ResolveResult[0];
        }

        final PsiManager instance = PsiManager.getInstance(project);
        final String finalPath = path;

        Collection<PsiElement> resultFilesCollection = new HashSet<PsiElement>();


        for (VirtualFile file : files) {
            if (finalPath != null && !finalPath.isEmpty()) {
                VirtualFile pathFile = file.getParent().findFileByRelativePath(finalPath);
                if (pathFile != null) {
                    file = pathFile;
                }
            }
            try {
                PsiFile psiFile = instance.findFile(file);
                if (psiFile != null) {
                    resultFilesCollection.add(instance.findFile(file));
                }
            } catch (Exception ignore) {
            }
        }

        return PsiElementResolveResult.createResults(resultFilesCollection);
    }
}
