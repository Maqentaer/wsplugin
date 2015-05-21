package ws.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import ws.WSUtil;

import java.util.Collection;
import java.util.HashSet;

public class WSJsReference extends WSPsiReference {

    public WSJsReference(PsiElement psiElement) {
        super(psiElement);
    }

    @NotNull
    public Object[] getVariants() {
        Collection<String> variants = WSUtil.getVariantsByName(parseResult, project);
        Collection<String> result = new HashSet<String>();

        for (Object variant : variants) {
            result.add("js!" + variant);
            result.addAll(WSUtil.getOtherModules((String) variant, project));
        }

        return result.toArray();
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
        return PsiElementResolveResult.createResults(WSUtil.resolveFilesByName(parseResult, project));
    }
}
