package ws.reference;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.WSUtil;


public abstract class WSPsiReference implements PsiReference, PsiPolyVariantReference {
    protected PsiElement element;
    protected TextRange textRange;
    protected Project project;
    protected String value;
    protected String[] parseResult;

    public WSPsiReference(PsiElement psiElement) {
        this.element = psiElement;
        this.project = psiElement.getProject();
        this.value = psiElement.getText().replaceAll("['\"]", "");
        this.textRange = new TextRange(1, (this.value.length() > 0 ? this.value.length() - 1 : 1));
        this.parseResult = WSUtil.parseComponentName(value);
    }

    @NotNull
    public abstract Object[] getVariants();

    @NotNull
    public abstract ResolveResult[] multiResolve(boolean b);

    @Nullable
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(true);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    public String toString() {
        return getCanonicalText();
    }

    public PsiElement getElement() {
        return this.element;
    }

    public TextRange getRangeInElement() {
        return textRange;
    }

    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        throw new IncorrectOperationException();
    }

    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        throw new IncorrectOperationException();
    }

    public boolean isReferenceTo(PsiElement element) {
        return resolve() == element;
    }

    public boolean isSoft() {
        return false;
    }

    @NotNull
    public String getCanonicalText() {
        return value;
    }
}
