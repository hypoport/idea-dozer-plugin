package de.hypoport.plugins.dozer.model;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.GenericDomValue;
import de.hypoport.plugins.dozer.converter.BeanFactoryConverter;
import de.hypoport.plugins.dozer.converter.ClassCreateMethodConverter;

public interface Class extends DozerDomElement, GenericDomValue<PsiClass> {

  @Convert(BeanFactoryConverter.class)
  GenericAttributeValue<PsiClass> getBeanFactory();

  GenericAttributeValue<String> getFactoryBeanId();

  GenericAttributeValue<String> getMapSetMethod();

  GenericAttributeValue<String> getMapGetMethod();

  @Convert(ClassCreateMethodConverter.class)
  GenericAttributeValue<PsiMethod> getCreateMethod();

  GenericAttributeValue<Boolean> getMapNull();

  GenericAttributeValue<Boolean> getEmptyString();
}
