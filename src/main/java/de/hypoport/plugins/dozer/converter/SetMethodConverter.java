package de.hypoport.plugins.dozer.converter;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.ResolvingConverter;
import de.hypoport.plugins.dozer.model.Class;
import de.hypoport.plugins.dozer.model.Mapping;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public class SetMethodConverter extends ResolvingConverter<PsiMethod> {

  @NotNull
  @Override
  public Collection<? extends PsiMethod> getVariants(ConvertContext context) {
    if (context.getInvocationElement() instanceof Class == false) {
      return Collections.emptyList();
    }
    Class clazz = (Class) context.getInvocationElement();

    Mapping mapping = DomUtil.getParentOfType(clazz, Mapping.class, false);
    if (mapping == null) {
      return Collections.emptyList();
    }

    PsiClass mappedClass;
    if (clazz.getXmlElementName().equals("a")) {
      mappedClass = mapping.getClassA().getValue();
    }
    else {
      mappedClass = mapping.getClassB().getValue();
    }

    if (mappedClass == null) {
      return Collections.emptyList();
    }

    return Collections.emptyList();
  }

  @Override
  public PsiMethod fromString(@Nullable String s, ConvertContext context) {
    return null;
  }

  @Override
  public String toString(@Nullable PsiMethod psiMethod, ConvertContext context) {
    return psiMethod == null ? null : psiMethod.getName();
  }
}
