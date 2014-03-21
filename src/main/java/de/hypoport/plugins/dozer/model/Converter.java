package de.hypoport.plugins.dozer.model;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;
import de.hypoport.plugins.dozer.converter.CustomConverterConverter;
import org.jetbrains.annotations.NotNull;

public interface Converter extends DozerDomElement {

  @NotNull
  Class getClassA();

  @NotNull
  Class getClassB();

  @Convert(CustomConverterConverter.class)
  @Required(value = true, nonEmpty = true)
  GenericAttributeValue<PsiClass> getType();
}
