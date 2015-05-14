package ws.reference;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import ws.WSUtil;

public class WSControlReference extends WSPsiReference {

    public WSControlReference(PsiElement psiElement) {
        super(psiElement);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return WSUtil.getVariantsByName(parseResult, project).toArray();
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
        if (WSUtil.matchPattern(value)) {
            return PsiElementResolveResult.createResults(WSUtil.resolveFilesByName(parseResult, project));
        } else {
            return ResolveResult.EMPTY_ARRAY;
        }
    }
}
