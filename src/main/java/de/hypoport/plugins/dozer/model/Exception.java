package de.hypoport.plugins.dozer.model;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.Required;
import de.hypoport.plugins.dozer.converter.ExceptionConverter;

@Convert(ExceptionConverter.class)
@Required(value = true, nonEmpty = true)
public interface Exception extends DozerDomElement, GenericDomValue<PsiClass> {

}
