package ws.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import ws.WSUtil;

import java.util.Collection;

public class WSJsReference extends WSPsiReference {

    public WSJsReference(PsiElement psiElement) {
        super(psiElement);
    }

    @NotNull
    public Object[] getVariants() {
        Collection<String> variants = WSUtil.getVariantsByName(parseResult, project);
        Object[] result = new Object[variants.size()];

        int i = 0;
        for (Object variant : variants) {
            result[i++] = "js!" + variant;
        }

        return result;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
        return PsiElementResolveResult.createResults(WSUtil.resolveFilesByName(parseResult, project));
    }
}
