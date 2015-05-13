package ws;

import com.intellij.lang.javascript.psi.JSArrayLiteralExpression;
import com.intellij.lang.javascript.psi.JSCallExpression;
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
        PsiElement parent;
        PsiElement topParent;

        try {
            parent = psiElement.getParent();
            topParent = parent.getParent().getParent();
        }catch (Exception e){
            return new PsiReference[0];
        }

        if (parent instanceof JSArrayLiteralExpression  && topParent instanceof JSCallExpression && (topParent.getText().startsWith("define") || topParent.getText().startsWith("require"))) {
            try {
                PsiReference ref = new WSJSReference(psiElement);
                return new PsiReference[]{ref};
            } catch (Exception ignore) {

            }
        }

        return new PsiReference[0];
    }
}
