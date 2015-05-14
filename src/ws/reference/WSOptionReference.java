package ws.reference;

import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.lang.javascript.psi.JSFile;
import com.intellij.lang.javascript.psi.JSFunctionExpression;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import ws.WSUtil;
import ws.amd.WSAMDFile;
import ws.amd.WSAMDUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class WSOptionReference extends WSPsiReference {
    public WSOptionReference(PsiElement psiElement) {
        super(psiElement);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Collection<PsiElement> resolveFiles = WSUtil.resolveFilesByName(parseResult, project);

        // todo: сделать в цикле
        if (resolveFiles.iterator().hasNext()) {
            JSFile file = (JSFile) resolveFiles.iterator().next();
            WSAMDFile amdFile = WSAMDUtils.getAMDFile(file);
            if (amdFile != null) {
                Set<String> func = amdFile.getFunctionDeclaration("js!" + this.parseResult[0] + this.parseResult[1] + ":");
                return func.toArray(new String[func.size()]);
            }
        }

        Object[] res = WSUtil.getVariantsByName(parseResult, project).toArray();
        for (int i = 0; i < res.length; i++) {
            res[i] = "js!" + res[i];
        }
        return res;
    }


    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
        String functionName = this.parseResult[2];

        if (functionName == null || functionName.isEmpty() || !value.startsWith("js!")) {
            return ResolveResult.EMPTY_ARRAY;
        }

        Collection<PsiElement> resolveFiles = WSUtil.resolveFilesByName(parseResult, project);
        Collection<PsiElement> result = new HashSet<PsiElement>();

        for (PsiElement file : resolveFiles) {
            WSAMDFile amdFile = WSAMDUtils.getAMDFile((PsiFile) file);
            if (amdFile != null) {
                Collection<JSFunctionExpression> functions = amdFile.getFunctions();

                for (JSFunctionExpression function : functions) {
                    JSElement parent = (JSElement) function.getParent();
                    if (parent instanceof JSProperty) {
                        String curFunctionName = function.getName() != null ? function.getName().replaceAll("['|\"]", "") : "";
                        if (function.getName() != null && functionName.matches("(prototype.)?" + curFunctionName)) {
                            result.add(function);
                        }
                    }
                }
            }
        }

        return PsiElementResolveResult.createResults(result);
    }
}
