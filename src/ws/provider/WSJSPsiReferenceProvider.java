package ws.provider;

import com.intellij.lang.javascript.psi.JSArgumentList;
import com.intellij.lang.javascript.psi.JSArrayLiteralExpression;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import ws.reference.WSJsReference;

public class WSJSPsiReferenceProvider extends PsiReferenceProvider {
    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        if (isReference(psiElement)) {
            try {
                PsiReference ref = new WSJsReference(psiElement);
                return new PsiReference[]{ref};
            } catch (Exception ignore) {

            }
        }

        return new PsiReference[0];
    }

    private boolean isReference(PsiElement psiElement) {
        PsiElement parent;
        PsiElement topParent;
        try {
            parent = psiElement.getParent();
            topParent = parent.getParent().getParent();
        } catch (Exception e) {
            return false;
        }
        return (
           parent instanceof JSArrayLiteralExpression && topParent instanceof JSCallExpression && (topParent.getText().startsWith("define")) ||
           parent instanceof JSArgumentList && (topParent.getText().startsWith("require") || topParent.getText().startsWith("$ws.require") ||
           topParent.getText().startsWith("$ws.requireModule")) || psiElement.getText().matches("^['\"]js!.*")
        );
    }
}
