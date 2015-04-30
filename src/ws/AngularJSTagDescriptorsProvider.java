package ws;

import com.intellij.codeInsight.completion.XmlTagInsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.html.impl.RelaxedHtmlFromSchemaElementDescriptor;
import com.intellij.lang.javascript.psi.impl.JSOffsetBasedImplicitElement;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.html.HtmlTag;
import com.intellij.psi.impl.source.xml.XmlDocumentImpl;
import com.intellij.psi.impl.source.xml.XmlElementDescriptorProvider;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;

import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xml.*;

import com.intellij.xml.impl.schema.AnyXmlAttributeDescriptor;
import com.intellij.xml.impl.schema.AnyXmlElementDescriptor;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.util.List;

public class AngularJSTagDescriptorsProvider implements XmlElementDescriptorProvider {

  @Nullable
  @Override
  public XmlElementDescriptor getDescriptor(final XmlTag xmlTag) {
    if (xmlTag.getName().equals("component")){
      return new XmlElementDescriptor(){

        @Override
        public PsiElement getDeclaration() {
          return xmlTag;
        }

        @Override
        public String getName(PsiElement psiElement) {
          return "component";
        }

        @Override
        public String getName() {
          return "component";
        }

        @Override
        public void init(PsiElement psiElement) {

        }

        @Override
        public Object[] getDependences() {
          return ArrayUtil.EMPTY_OBJECT_ARRAY;
        }

        @Override
        public String getQualifiedName() {
          return "component";
        }

        @Override
        public String getDefaultName() {
          return "component";
        }

        @Override
        public XmlElementDescriptor[] getElementsDescriptors(XmlTag xmlTag) {
          XmlDocumentImpl xmlDocument = PsiTreeUtil.getParentOfType(xmlTag, XmlDocumentImpl.class);
          if (xmlDocument == null) return EMPTY_ARRAY;
          return xmlDocument.getRootTagNSDescriptor().getRootElementsDescriptors(xmlDocument);
        }

        @Nullable
        @Override
        public XmlElementDescriptor getElementDescriptor(XmlTag childTag, XmlTag contextTag) {
          XmlTag parent = contextTag.getParentTag();
          if (parent == null) return null;
          final XmlNSDescriptor descriptor = parent.getNSDescriptor(childTag.getNamespace(), true);
          return descriptor == null ? null : descriptor.getElementDescriptor(childTag);
        }

        @Override
        public XmlAttributeDescriptor[] getAttributesDescriptors(@Nullable XmlTag context) {
          return XmlAttributeDescriptor.EMPTY;
        }

        @Nullable
        @Override
        public XmlAttributeDescriptor getAttributeDescriptor(XmlAttribute attribute) {
          return getAttributeDescriptor(attribute.getName(), attribute.getParent());
        }

        @Nullable
        @Override
        public XmlAttributeDescriptor getAttributeDescriptor(@NonNls final String attributeName, @Nullable XmlTag context) {
          return ContainerUtil.find(getAttributesDescriptors(context), new Condition<XmlAttributeDescriptor>() {
            @Override
            public boolean value(XmlAttributeDescriptor descriptor) {
              return attributeName.equals(descriptor.getName());
            }
          });
        }

        @Override
        public XmlNSDescriptor getNSDescriptor() {
          return null;
        }

        @Nullable
        @Override
        public XmlElementsGroup getTopGroup() {
          return null;
        }

        @Override
        public int getContentType() {
          return CONTENT_TYPE_ANY;
        }

        @Nullable
        @Override
        public String getDefaultValue() {
          return "";
        }
      };
    }

   return null;
  }
}
