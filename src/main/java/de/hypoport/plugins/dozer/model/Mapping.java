package de.hypoport.plugins.dozer.model;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import de.hypoport.plugins.dozer.converter.BeanFactoryConverter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Mapping extends DozerDomElement {

  Class getClassA();

  Class getClassB();

  @NotNull
  List<Field> getFields();

  Field addField();

  @NotNull
  List<FieldExclude> getFieldExcludes();

  FieldExclude addFieldExclude();

  GenericAttributeValue<String> getDateFormat();

  GenericAttributeValue<Boolean> getStopOnErrors();

  GenericAttributeValue<Boolean> getWildcard();

  GenericAttributeValue<Boolean> getTrimStrings();

  GenericAttributeValue<Boolean> getMapNull();

  GenericAttributeValue<Boolean> getMapEmptyString();

  @Convert(BeanFactoryConverter.class)
  GenericAttributeValue<PsiClass> getBeanFactory();

  GenericAttributeValue<Type> getType();

  GenericAttributeValue<RelationshipType> getRelationshipType();

  GenericAttributeValue<String> getMapId();
}
