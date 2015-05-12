package ws;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.index.WSFileBasedIndexExtension;

import java.util.Collection;
import java.util.HashSet;

public class WSJSPsiReference implements PsiReference, PsiPolyVariantReference {
    private String value;
    private Project project;
    private PsiElement psiElement;
    private TextRange textRange;

    public WSJSPsiReference(PsiElement psiElement) {
        this.psiElement = psiElement;
        this.project = psiElement.getProject();
        this.value = psiElement.getText().replaceAll("['\"]", "");
        this.textRange = new TextRange(1, this.value.length() - 1);
    }

    @NotNull
    public Object[] getVariants() {
        Collection<String> result = new HashSet<String>();
        Collection<String> result2= new HashSet<String>();
        String[] parseResult = WSUtil.parseComponentName(psiElement.getText());
        String componentName = parseResult[0];
        String postKey = parseResult[1];

        if(!componentName.isEmpty()){
            Collection<VirtualFile> files = WSFileBasedIndexExtension.getFileByComponentName(project, componentName);
            for(VirtualFile file : files){
                if(!postKey.isEmpty()){
                    result.addAll(WSUtil.getChildFiles(file, componentName));
                }
            }

            if(result2.size() > 0){
                return  result2.toArray();
            }
        } else {
            result = WSFileBasedIndexExtension.getAllComponentNames(project);
        }


        for(String temp: result){
            result2.add("js!" + temp);
        }
        return  result2.toArray();

    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {


        String[] parseResult = WSUtil.parseComponentName(value);

        String controlName = parseResult[0];
        String path = parseResult[1];

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
                VirtualFile pathFile = file.getParent().findFileByRelativePath(path + ".js");
                if (pathFile != null) {
                    file = pathFile;
                } else {
                    continue;
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
        ResolveResult[] resolveResults = multiResolve(true);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @Override
    public String toString() {
        return getCanonicalText();
    }

    public PsiElement getElement() {
        return this.psiElement;
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
