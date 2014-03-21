package de.hypoport.plugins.dozer.converter;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.ResolvingConverter;
import de.hypoport.plugins.dozer.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public abstract class ClassInheritorConverter extends ResolvingConverter<PsiClass> {

  private String baseClassName;
  private PsiClassAcceptor acceptor;

  public static final PsiClassAcceptor ALL = new PsiClassAcceptor() {
    @Override
    public boolean accepts(PsiClass clazz) {
      return true;
    }
  };

  public static final PsiClassAcceptor CONCRETE_CLASSES_ONLY = new PsiClassAcceptor() {
    @Override
    public boolean accepts(PsiClass clazz) {
      return clazz.isAnnotationType() == false
             && clazz.isEnum() == false
             && clazz.isInterface() == false
             && clazz.getModifierList().hasModifierProperty(PsiModifier.ABSTRACT) == false;
    }
  };

  public ClassInheritorConverter(@NotNull String baseClassName) {
    this(baseClassName, ALL);
  }

  public ClassInheritorConverter(@NotNull String baseClassName, @NotNull PsiClassAcceptor acceptor) {
    this.baseClassName = baseClassName;
    this.acceptor = acceptor;
  }

  @NotNull
  @Override
  public Collection<? extends PsiClass> getVariants(ConvertContext context) {
    PsiClass beanFactoryClass = PsiUtil.findClass(context.getModule().getProject(), baseClassName);

    if (beanFactoryClass == null) {
      return Collections.emptyList();
    }

    if (acceptor == ALL) {
      return ClassInheritorsSearch.search(beanFactoryClass).findAll();
    }
    else {
      LinkedList<PsiClass> result = new LinkedList<PsiClass>();
      for (PsiClass clazz : ClassInheritorsSearch.search(beanFactoryClass).findAll()) {
        if (acceptor.accepts(clazz)) {
          result.add(clazz);
        }
      }
      return result;
    }
  }

  @Override
  public PsiClass fromString(@Nullable String s, ConvertContext context) {
    if (s == null) {
      return null;
    }

    for (PsiClass beanFactoryClass : getVariants(context)) {
      if (beanFactoryClass.getQualifiedName().equals(s)) {
        return beanFactoryClass;
      }
    }

    return null;
  }

  @Override
  public String toString(@Nullable PsiClass psiClass, ConvertContext context) {
    return psiClass == null ? null : psiClass.getQualifiedName();
  }

  public static interface PsiClassAcceptor {

    public boolean accepts(PsiClass clazz);
  }
}
