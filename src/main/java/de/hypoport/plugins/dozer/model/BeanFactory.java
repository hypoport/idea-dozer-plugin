package de.hypoport.plugins.dozer.model;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericDomValue;
import de.hypoport.plugins.dozer.converter.BeanFactoryConverter;

@Convert(BeanFactoryConverter.class)
public interface BeanFactory extends DozerDomElement, GenericDomValue<PsiClass> {

}
