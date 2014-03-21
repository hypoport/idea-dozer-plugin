package de.hypoport.plugins.dozer.model;

import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.GenericDomValue;
import de.hypoport.plugins.dozer.converter.FieldExpressionConverter;

@Convert(FieldExpressionConverter.class)
public interface FieldDefinition extends DozerDomElement, GenericDomValue<FieldExpression> {

  GenericAttributeValue<String> getDateFormat();

  GenericAttributeValue<FieldType> getFieldType();

  GenericAttributeValue<String> getSetMethod();

  GenericAttributeValue<String> getGetMethod();

  GenericAttributeValue<String> getKey();

  GenericAttributeValue<String> getMapSetMethod();

  GenericAttributeValue<String> getMapGetMethod();

  GenericAttributeValue<Boolean> getIsAccessible();

  GenericAttributeValue<String> getCreateMethod();
}
