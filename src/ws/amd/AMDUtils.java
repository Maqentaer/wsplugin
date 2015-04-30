package ws.amd;

import com.intellij.lang.javascript.psi.*;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class AMDUtils {


    @Nullable
    public static AMDFile getDefineStatementItems(PsiFile file) {
        final AMDFile[] itemsArray = new AMDFile[1];
        final Set<JSFunctionExpression> protoFunctions = new HashSet<JSFunctionExpression>();
        final Set<JSFunctionExpression> functions = new HashSet<JSFunctionExpression>();

        file.acceptChildren(new JSRecursiveElementVisitor() {
            @Override
            public void visitJSCallExpression(JSCallExpression element) {
                if (element.getMethodExpression().getText().equals("define")) {
                    AMDFile items = getDefineStatementItemsFromArguments(element.getArguments(), element);
                    if (items == null) {
                        itemsArray[0] = null;
                        return;
                    }
                    itemsArray[0] = getDefineStatementItemsFromArguments(element.getArguments(), element);
                } else if (element.getMethodExpression().getText().endsWith("extend")) {
                    JSExpression[] arguments = element.getArguments();
                    JSObjectLiteralExpression obj = null;
                    if (arguments.length == 1) {
                        obj = arguments[0] instanceof JSObjectLiteralExpression ? (JSObjectLiteralExpression) arguments[0] : null;
                    } else if (arguments.length == 2) {
                        obj = arguments[1] instanceof JSObjectLiteralExpression ? (JSObjectLiteralExpression) arguments[1] : null;
                    }

                    if (obj != null) {
                        JSProperty[] props = obj.getProperties();
                        for (JSProperty prop : props) {
                            if (prop.getValue() instanceof JSFunctionExpression) {
                                protoFunctions.add((JSFunctionExpression) prop.getValue());
                            }
                        }
                    }
                    return;
                }
                super.visitJSCallExpression(element);
            }
        });

        itemsArray[0].setProtoFunctions(protoFunctions);
        itemsArray[0].setFunctions(functions);

        return itemsArray[0];
    }

    public static AMDFile getDefineStatementItemsFromArguments(JSExpression[] arguments, JSCallExpression original) {
        // account for when we get this (even though this is defined as legacy) :
        /**
         * define('classname', [], function(...){});
         */
        int argumentOffset = 0;
        String className = null;

        if (arguments.length > 1 && arguments[0] instanceof JSLiteralExpression && arguments[1] instanceof JSArrayLiteralExpression) {
            argumentOffset = 1;
            className = arguments[0].getText();
        } else if (!(arguments.length > 1 && arguments[0] instanceof JSArrayLiteralExpression && arguments[1] instanceof JSFunctionExpression)) {
            return null;
        }

        JSArrayLiteralExpression literalExpressions = (JSArrayLiteralExpression) arguments[argumentOffset];
        JSFunctionExpression function = (JSFunctionExpression) arguments[1 + argumentOffset];

        return new AMDFile(literalExpressions, function, className, original);
    }
}
