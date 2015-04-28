package ws;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.intellij.psi.impl.source.xml.XmlAttributeImpl;
import org.jetbrains.annotations.NotNull;

public class WSPsiReferenceProvider extends PsiReferenceProvider {
    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        Project project = psiElement.getProject();
        String path = psiElement.getText();
        VirtualFile appDir = project.getBaseDir();

        XmlAttributeImpl attr = (XmlAttributeImpl) psiElement.getParent();
        String attName = attr.getName();
        String tagName = attr.getParent().getName();
        String attrValue = attr.getValue();

        if (tagName.equals("component") && attName.equals("data-component") && attrValue != null && !attrValue.isEmpty()) {
            try {
                PsiReference ref = new WSPsiReference(attrValue, psiElement, new TextRange(1, path.length() - 1), project, appDir);
                return new PsiReference[]{ref};
            } catch (Exception ignored) {
            }
        }
        return new PsiReference[0];
    }
}
