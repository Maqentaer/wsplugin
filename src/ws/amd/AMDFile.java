package ws.amd;

import com.intellij.lang.javascript.psi.JSArrayLiteralExpression;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSFunctionExpression;
import com.intellij.patterns.CollectionPattern;
import com.intellij.util.containers.ContainerUtil;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class AMDFile {
    private JSArrayLiteralExpression arguments;
    private JSFunctionExpression function;
    private String className;
    public boolean hasConstructor;
    private JSCallExpression callExpression;
    private Set<JSFunctionExpression> protoFunctions;
    private Set<JSFunctionExpression> functions;

    public AMDFile(JSArrayLiteralExpression arguments, JSFunctionExpression function, String className, JSCallExpression originalParent) {
        this.arguments = arguments;
        this.function = function;
        this.className = className;
        this.callExpression = originalParent;
        this.hasConstructor = false;
    }

    public Set<String> getProtoFunctionDeclaration(String prefix){
        Set<String> res = new HashSet<String>();
        for( JSFunctionExpression function : hasConstructor ? protoFunctions : functions){
            String name = function.getName();
            if(name != null && !name.isEmpty()){
                res.add(prefix + (hasConstructor ? "prototype." : "") + function.getName().replaceAll("['\"]",""));
            }
        }
        return res;
    }

    public JSArrayLiteralExpression getArguments() {
        return arguments;
    }

    public JSFunctionExpression getFunction() {
        return function;
    }

    @Nullable
    public String getClassName() {
        return className;
    }

    public JSCallExpression getCallExpression() {
        return callExpression;
    }

    public void setFunctions(Set<JSFunctionExpression> func){
        functions = func;
    }

    public void setProtoFunctions(Set<JSFunctionExpression> func){
        protoFunctions = func;
    }

    public Set<JSFunctionExpression> getFunctions(){
        return functions;
    }

    public Set<JSFunctionExpression> getProtoFunctions(){
        return protoFunctions;
    }
}
