package de.hypoport.plugins.dozer.model;

import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.Required;

public interface Variable extends DozerDomElement, GenericDomValue<String> {

  @Required(value = true, nonEmpty = true)
  GenericAttributeValue<String> getName();
}
