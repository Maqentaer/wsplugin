package ws;

import com.intellij.lang.javascript.psi.impl.JSLiteralExpressionImpl;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.filters.ElementFilter;
import com.intellij.psi.filters.position.FilterPattern;
import com.intellij.psi.impl.source.xml.XmlAttributeValueImpl;
import com.intellij.psi.impl.source.xml.XmlTokenImpl;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlText;
import org.jetbrains.annotations.NotNull;
import ws.provider.WSJSPsiReferenceProvider;
import ws.provider.WSPsiReferenceProvider;
import ws.provider.WSXmlPsiReferenceProvider;


public class WSPsiReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar psiReferenceRegistrar) {

        psiReferenceRegistrar.registerReferenceProvider(StandardPatterns.instanceOf(XmlAttributeValueImpl.class), new WSPsiReferenceProvider());

        psiReferenceRegistrar.registerReferenceProvider(StandardPatterns.instanceOf(JSLiteralExpressionImpl.class), new WSJSPsiReferenceProvider());

        psiReferenceRegistrar.registerReferenceProvider(
                StandardPatterns
                        .instanceOf(XmlTokenImpl.class) // !!! почему-то не получается сразу искать в XmlText, поэтому ищем XmlToken
                        .and(new FilterPattern(new ElementFilter() {
                            @Override
                            public boolean isAcceptable(Object element, PsiElement context) {
                                try {
                                    PsiElement xmlText = context.getParent();
                                    PsiElement xmlTag = xmlText.getParent();
                                    return (
                                        context.toString().equals("XmlToken:XML_DATA_CHARACTERS") &&
                                        xmlText instanceof XmlText &&
                                        context.getText().equals(xmlText.getText()) &&
                                        xmlTag instanceof XmlTag &&
                                        ((XmlTag) xmlTag).getName().equals("option")/* &&
                                        (
                                            (
                                                ((XmlTag) xmlTag).getAttribute("type") != null &&
                                                ((XmlTag) xmlTag).getAttribute("type").getValue().equals("function")
                                            ) ||
                                            xmlText.getText().startsWith("js!")
                                        )*/
                                    );
                                } catch (Exception e) {
                                    return false;
                                }
                            }

                            @Override
                            public boolean isClassAcceptable(Class aClass) { return true; }
                        })),
                new WSXmlPsiReferenceProvider()
        );
    }
}
