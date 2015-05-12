package ws;

import com.intellij.lang.javascript.frameworks.amd.JSAmdUtil;
import com.intellij.lang.javascript.psi.impl.JSLiteralExpressionImpl;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.impl.source.xml.XmlAttributeImpl;
import com.intellij.psi.impl.source.xml.XmlAttributeValueImpl;
import com.intellij.psi.impl.source.xml.XmlTagImpl;
import org.jetbrains.annotations.NotNull;


public class WSPsiReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar psiReferenceRegistrar) {
        psiReferenceRegistrar.registerReferenceProvider(StandardPatterns.instanceOf(XmlAttributeValueImpl.class), new WSPsiReferenceProvider());
        psiReferenceRegistrar.registerReferenceProvider(StandardPatterns.instanceOf(JSLiteralExpressionImpl.class), new WSJSPsiReferenceProvider());
    }
}
