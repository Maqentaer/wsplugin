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
                    AMDFile item = getDefineStatementItemsFromArguments(element.getArguments(), element);
                    if (item == null) {
                        itemsArray[0] = null;
                        return;
                    }
                    itemsArray[0] = item;
                } else if (itemsArray[0] != null && element.getMethodExpression().getText().endsWith("extend")) {
                    itemsArray[0].hasConstructor = true;
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

        if(itemsArray[0] != null) {
            if (itemsArray[0].hasConstructor) {
                itemsArray[0].setProtoFunctions(protoFunctions);
            } else {
                file.acceptChildren(new JSRecursiveElementVisitor() {
                    @Override
                    public void visitJSFunctionExpression(JSFunctionExpression element) {
                        if (element.getParent() instanceof JSProperty) {
                            functions.add(element);
                        }
                        super.visitJSFunctionExpression(element);
                    }
                });
                itemsArray[0].setFunctions(functions);
            }
        }

        return itemsArray[0];
    }

    private static AMDFile getDefineStatementItemsFromArguments(JSExpression[] arguments, JSCallExpression original) {
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
