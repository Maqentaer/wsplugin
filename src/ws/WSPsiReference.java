package ws;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.CommonProcessors;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.indexing.FileBasedIndex;
import ws.index.WSFileBasedIndexExtension;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


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

    @NotNull
    public Object[] getVariants() {
        /*Collection<String> result = new HashSet<String>();
        Collection<String> keys = WSFileBasedIndexExtension.getAllComponentNames(project);
        for(String key : keys){
            result.add(key);
            VirtualFile file = WSFileBasedIndexExtension.getFirstFileByComponentName(project, key);
            if(file != null){
                String filePath = file.getPath().replaceAll(file.getName()+"$", "");
                VirtualFile[] childFiles = file.getParent().getChildren();
                for(VirtualFile childFile : childFiles){
                    if(childFile.isDirectory()){
                        VirtualFile[] childFiles2 = childFile.getParent().getChildren();
                        for(VirtualFile childFile2 : childFiles){
                            if(childFile.getName().endsWith(".js")){
                                result.add(key + childFile2.getPath().replace(filePath, ""));
                            }
                        }
                    }else if(childFile.getName().endsWith(".js")){
                        result.add(key + childFile.getPath().replace(filePath, ""));
                    }
                }
            }
        }
        return result.toArray();*/
        return WSFileBasedIndexExtension.getAllComponentNames(project).toArray();
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
        String controlName = null;
        String path = null;

        Pattern pattern = Pattern.compile("(SBIS3\\.\\w+\\.\\w+)(.*)?");
        Matcher matcher = pattern.matcher(value);

        if (matcher.find()) {
            controlName = matcher.group(1);
            path = matcher.group(2);
        }

        if (path != null && !path.isEmpty() && !path.endsWith(".js")) {
            return new ResolveResult[0];
        }

        if (controlName == null || controlName.isEmpty()) {
            return new ResolveResult[0];
        }

        Collection<VirtualFile> files = WSFileBasedIndexExtension.getFileByComponentName(project, controlName);

        if (files.size() == 0) {
            return new ResolveResult[0];
        }

        final PsiManager instance = PsiManager.getInstance(project);

        Collection<PsiElement> resultFilesCollection = new HashSet<PsiElement>();


        for (VirtualFile file : files) {
            if (path != null && !path.isEmpty()) {
                VirtualFile pathFile = file.getParent().findFileByRelativePath(path);
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

    @Nullable
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
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

    public boolean isSoft() {
        return false;
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return value;
    }
}
