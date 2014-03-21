package de.hypoport.plugins.dozer.model;

import com.intellij.util.xml.GenericAttributeValue;

public interface FieldExclude extends DozerDomElement {

  FieldDefinition getA();

  FieldDefinition getB();

  GenericAttributeValue<Type> getType();
}
