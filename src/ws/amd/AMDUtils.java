package ws.amd;

import com.intellij.lang.javascript.psi.*;
import com.intellij.psi.PsiFile;

import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

public class AMDUtils {

    @Nullable
    public static AMDFile getAMDFile(final PsiFile file) {
        final AMDFile[] itemsArray = new AMDFile[1];

        file.acceptChildren(new JSRecursiveElementVisitor() {
            @Override
            public void visitJSCallExpression(JSCallExpression element) {
                if (element.getMethodExpression().getText().equals("define")) {
                    AMDFile amdFile = createAMDFile(file, element.getArguments());
                    if (amdFile == null) {
                        itemsArray[0] = null;
                        return;
                    }
                    itemsArray[0] = amdFile;
                } else if (itemsArray[0] != null && element.getMethodExpression().getText().endsWith("extend")) {
                    itemsArray[0].hasConstructor = true;
                    itemsArray[0].setProtoFunctions(getPrototypeFunctions(element.getArguments()));
                    return;
                }
                super.visitJSCallExpression(element);
            }
        });

        if (itemsArray[0] != null) {
            if (!itemsArray[0].hasConstructor) {
                itemsArray[0].setFunctions(getAllFunctionDeclarations(file));
            }
        }

        return itemsArray[0];
    }

    private static Set<JSFunctionExpression> getPrototypeFunctions(JSExpression[] arguments) {
        final Set<JSFunctionExpression> protoFunctions = new HashSet<JSFunctionExpression>();
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
        return protoFunctions;
    }

    private static Set<JSFunctionExpression> getAllFunctionDeclarations(PsiFile file) {
        final Set<JSFunctionExpression> functions = new HashSet<JSFunctionExpression>();
        file.acceptChildren(new JSRecursiveElementVisitor() {
            @Override
            public void visitJSFunctionExpression(JSFunctionExpression element) {
                if (element.getParent() instanceof JSProperty) {
                    functions.add(element);
                }
                super.visitJSFunctionExpression(element);
            }
        });
        return functions;
    }

    @Nullable
    private static AMDFile createAMDFile(PsiFile file, JSExpression[] arguments) {
        if (arguments.length == 3 && arguments[0] instanceof JSLiteralExpression && arguments[1] instanceof JSArrayLiteralExpression && arguments[2] instanceof JSFunctionExpression) {
            return new AMDFile(file, arguments[0].getText(), (JSArrayLiteralExpression) arguments[1]);
        } else if (arguments.length == 2 && arguments[0] instanceof JSArrayLiteralExpression && arguments[1] instanceof JSFunctionExpression) {
            return new AMDFile(file, null, (JSArrayLiteralExpression) arguments[0]);
        } else {
            return null;
        }
    }

}
