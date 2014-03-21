package de.hypoport.plugins.dozer.model;

import com.intellij.util.xml.GenericDomValue;

public interface Configuration extends DozerDomElement {

  GenericDomValue<Boolean> getStopOnErrors();

  GenericDomValue<String> getDateFormat();

  GenericDomValue<Boolean> getWildcard();

  GenericDomValue<Boolean> getTrimStrings();

  BeanFactory getBeanFactory();

  GenericDomValue<RelationshipType> getRelationshipType();

  CustomConverters getCustomConverters();

  CopyByReferences getCopyByReferences();

  AllowedExceptions getAllowedExceptions();

  Variables getVariables();
}
