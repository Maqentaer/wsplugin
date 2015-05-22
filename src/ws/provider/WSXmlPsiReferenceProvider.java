package ws.provider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import ws.reference.WSOptionReference;

public class WSXmlPsiReferenceProvider extends PsiReferenceProvider {
    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        try {
            PsiReference ref = new WSOptionReference(psiElement.getParent());
            return new PsiReference[]{ref};
        } catch (Exception ignored) {
        }
        return new PsiReference[0];
    }
}
