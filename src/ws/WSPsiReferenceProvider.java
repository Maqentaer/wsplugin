package ws;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
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

        XmlAttributeImpl attr;
        try {
            attr = (XmlAttributeImpl) psiElement.getParent();
        } catch (Exception e){
            return new PsiReference[0];
        }


        XmlTag tag = attr.getParent();
        XmlAttribute type = tag.getAttribute("type");

        String attName = attr.getName();
        String tagName = tag.getName();
        String attrValue = attr.getValue();

        if (attrValue != null && !attrValue.isEmpty()) {
            if(tagName.equals("component") && attName.equals("data-component")){
                try {
                    PsiReference ref = new WSPsiReference(attrValue, psiElement, new TextRange(1, path.length() - 1), project, appDir);
                    return new PsiReference[]{ref};
                } catch (Exception ignored) {
                }
            } else if(tagName.equals("option") && attName.equals("value")){
                if(type != null && type.getValue() != null && type.getValue().equals("function")){
                    try {
                        PsiReference ref = new WSOptionPsiReference(attrValue, psiElement, new TextRange(1, path.length() - 1), project, appDir);
                        return new PsiReference[]{ref};
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        return new PsiReference[0];
    }
}
