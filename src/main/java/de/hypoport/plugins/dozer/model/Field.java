package de.hypoport.plugins.dozer.model;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.GenericDomValue;
import de.hypoport.plugins.dozer.converter.CustomConverterConverter;

public interface Field extends DozerDomElement {

  FieldDefinition getA();

  FieldDefinition getB();

  GenericDomValue<String> getAHint();

  GenericDomValue<String> getBHint();

  GenericDomValue<String> getADeepIndexHint();

  GenericDomValue<String> getBDeepIndexHint();

  GenericAttributeValue<RelationshipType> getRelationshipType();

  GenericAttributeValue<Boolean> getRemoveOrphans();

  GenericAttributeValue<Type> getType();

  GenericAttributeValue<String> getMapId();

  GenericAttributeValue<Boolean> getCopyByReference();

  @Convert(CustomConverterConverter.class)
  GenericAttributeValue<PsiClass> getCustomConverter();

  GenericAttributeValue<String> getCustomConverterId();

  GenericAttributeValue<String> getCustomConverterParam();
}
