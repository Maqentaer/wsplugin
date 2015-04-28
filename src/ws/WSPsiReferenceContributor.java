package ws;

import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.impl.source.xml.XmlAttributeValueImpl;
import org.jetbrains.annotations.NotNull;


public class WSPsiReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar psiReferenceRegistrar) {
        WSPsiReferenceProvider provider = new WSPsiReferenceProvider();
        psiReferenceRegistrar.registerReferenceProvider(StandardPatterns.instanceOf(XmlAttributeValueImpl.class), provider);
    }
}
