package ws;

import com.intellij.lang.javascript.psi.JSArrayLiteralExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import ws.reference.WSJSReference;

public class WSJSPsiReferenceProvider extends PsiReferenceProvider {
    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        if (psiElement.getParent() instanceof JSArrayLiteralExpression) {
            try {
                PsiReference ref = new WSJSReference(psiElement);
                return new PsiReference[]{ref};
            } catch (Exception ignore) {

            }
        }

        return new PsiReference[0];
    }
}
