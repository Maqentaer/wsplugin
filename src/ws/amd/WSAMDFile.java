package ws.amd;

import com.intellij.lang.javascript.psi.JSArrayLiteralExpression;
import com.intellij.lang.javascript.psi.JSFunctionExpression;
import com.intellij.psi.PsiFile;

import java.util.HashSet;
import java.util.Set;

public class WSAMDFile {
    private PsiFile file;
    private JSArrayLiteralExpression arguments;
    private String className;
    private Set<JSFunctionExpression> protoFunctions;
    private Set<JSFunctionExpression> functions;

    public boolean hasConstructor;

    public WSAMDFile(PsiFile file, String className, JSArrayLiteralExpression arguments) {
        this.file = file;
        this.arguments = arguments;
        this.className = className;
        this.hasConstructor = false;
    }

    public Set<String> getFunctionDeclaration(String prefix) {
        Set<String> res = new HashSet<String>();
        for (JSFunctionExpression function : hasConstructor ? protoFunctions : functions) {
            String name = function.getName();
            if (name != null && !name.isEmpty()) {
                res.add(prefix + (hasConstructor ? "prototype." : "") + function.getName().replaceAll("['\"]", ""));
            }
        }
        return res;
    }

    public PsiFile getFile() {
        return file;
    }

    public JSArrayLiteralExpression getArguments() {
        return arguments;
    }

    public String getName() {
        return className;
    }

    public void setFunctions(Set<JSFunctionExpression> func) {
        functions = func;
    }

    public void setProtoFunctions(Set<JSFunctionExpression> func) {
        protoFunctions = func;
    }

    public Set<JSFunctionExpression> getFunctions() {
        return hasConstructor ? protoFunctions : functions;
    }

    public Set<JSFunctionExpression> getAllFunctions() {
        return functions;
    }

    public Set<JSFunctionExpression> getProtoFunctions() {
        return protoFunctions;
    }
}
