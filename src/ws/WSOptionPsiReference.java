package ws;

import com.intellij.lang.javascript.psi.*;
import com.intellij.lang.javascript.psi.impl.JSReturnStatementImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import ws.amd.AMDUtils;
import ws.amd.AMDFile;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class WSOptionPsiReference extends WSPsiReference {
    public WSOptionPsiReference(String value, PsiElement element, TextRange textRange, Project project, VirtualFile appDir) {
        super(value, element, textRange, project, appDir);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        String functionName = this.parseResult[2];

        if (!functionName.isEmpty()) {
            ResolveResult[] resolveResults = super.multiResolve(true);
            if (resolveResults.length > 0 && resolveResults[0] != null) {
                JSFile file = (JSFile) resolveResults[0].getElement();
                AMDFile amdFile = AMDUtils.getDefineStatementItems(file);
                if (amdFile != null) {
                    Set<String> func = amdFile.getProtoFunctionDeclaration("js!" + this.parseResult[0] + this.parseResult[1] + ":");
                    return func.toArray(new String[func.size()]);
                }
            }
        }

        Object[] res = super.getVariants();
        for (int i = 0; i < res.length; i++) {
            res[i] = "js!" + res[i];
        }
        return res;
    }


    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {

        if (!value.startsWith("js!")) {
            return new ResolveResult[0];
        }

        String functionName = this.parseResult[2];
        ResolveResult[] resolveResults = super.multiResolve(b);


        if (!functionName.isEmpty() && resolveResults.length > 0) {
            Collection<PsiElement> result = new HashSet<PsiElement>();
            JSFile file = (JSFile) resolveResults[0].getElement();


            AMDFile amdFile = AMDUtils.getDefineStatementItems(file);
            if (amdFile != null) {
                Collection<JSFunctionExpression> functions;
                if(amdFile.hasConstructor){
                    functions = amdFile.getProtoFunctions();
                } else {
                    functions = amdFile.getFunctions();
                }

                for (JSFunctionExpression function : functions) {
                    JSElement parent = (JSElement) function.getParent();
                    if (parent instanceof JSProperty) {
                        String curFunctionName = function.getName() != null ? function.getName().replaceAll("['|\"]", "") : "";
                        if (function.getName() != null && functionName.matches("(prototype.)?" + curFunctionName)) {
                            result.add(function);
                        }
                    }
                }
                return PsiElementResolveResult.createResults(result);
            }
        }

        return resolveResults;
    }
}
