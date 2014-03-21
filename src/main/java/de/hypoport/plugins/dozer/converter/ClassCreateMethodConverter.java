package de.hypoport.plugins.dozer.converter;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.ResolvingConverter;
import de.hypoport.plugins.dozer.PsiUtil;
import de.hypoport.plugins.dozer.model.Class;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ClassCreateMethodConverter extends ResolvingConverter<PsiMethod> {

  @NotNull
  @Override
  public Collection<? extends PsiMethod> getVariants(ConvertContext context) {
    Class clazz = getClass(context);

    if (clazz == null
        || clazz.getValue() == null) {
      return Collections.emptyList();
    }

    final String createMethodReturnType = clazz.getValue().getQualifiedName();

    LinkedList<PsiMethod> result = new LinkedList<>();
    result.addAll(findCreateMethods(clazz.getValue(), createMethodReturnType));

//      This takes much too long:
//        
//        final Project project = context.getModule().getProject();
//
//        Iterator<PsiClass> classIterator = AllClassesSearch.search(GlobalSearchScope.allScope(project), project).iterator();
//        while (classIterator.hasNext()) {
//            PsiClass foundClass = classIterator.next();
//            if (foundClass.equals(clazz)) {
//                continue;
//            }
//            result.addAll(findCreateMethods(foundClass, createMethodReturnType));
//        }

    return result;
  }

  private static boolean isCreateMethod(PsiMethod method, String expectedReturnType) {
    return (!method.isConstructor()
            && method.getModifierList().hasModifierProperty("public")
            && method.getModifierList().hasModifierProperty("static")
            && method.getParameterList().getParametersCount() == 0
            && method.getReturnType().getCanonicalText().equals(expectedReturnType));
  }

  @NotNull
  protected List<PsiMethod> findCreateMethods(@NotNull PsiClass psiClass, @NotNull String expectedReturnType) {
    LinkedList<PsiMethod> result = new LinkedList<PsiMethod>();
    for (PsiMethod method : psiClass.getMethods()) {
      if (isCreateMethod(method, expectedReturnType)) {
        result.add(method);
      }
    }
    return result;
  }

  @Override
  public PsiMethod fromString(@Nullable String s, ConvertContext context) {
    if (s == null) {
      return null;
    }

    Class clazz = getClass(context);

    if (clazz == null) {
      return null;
    }

    return resolveCreateMethod(clazz, s);
  }

  public static PsiMethod resolveCreateMethod(Class clazz, String methodExpression) {
    if (clazz.getValue() == null) {
      return null;
    }

    final String createMethodReturnType = clazz.getValue().getQualifiedName();

    final Project project = clazz.getModule().getProject();

    int lastDot = methodExpression.lastIndexOf('.');
    if (lastDot == -1) {
      for (PsiMethod method : clazz.getValue().findMethodsByName(methodExpression, false)) {
        if (isCreateMethod(method, createMethodReturnType)) {
          return method;
        }
      }
    }
    else if (lastDot > 0 && (lastDot + 1) < methodExpression.length()) {
      String className = methodExpression.substring(0, lastDot);
      String methodName = methodExpression.substring(lastDot + 1);

      PsiClass matchedClass = PsiUtil.findClass(project, className);
      for (PsiMethod method : matchedClass.findMethodsByName(methodName, false)) {
        if (isCreateMethod(method, createMethodReturnType)) {
          return method;
        }
      }
    }

    return null;
  }

  public static PsiMethod resolveCreateMethod(Class clazz) {
    if (clazz.getValue() == null) {
      return null;
    }

    return resolveCreateMethod(clazz, clazz.getCreateMethod().getStringValue());
  }

  @Override
  public String toString(@Nullable PsiMethod psiMethod, ConvertContext context) {
    if (psiMethod == null) {
      return null;
    }

    Class clazz = getClass(context);

    if (clazz == null
        || clazz.getValue() == null) {
      return null;
    }

    if (clazz.getValue().equals(psiMethod.getContainingClass())) {
      return psiMethod.getName();
    }
    else {
      return psiMethod.getContainingClass().getQualifiedName()
             + '.'
             + psiMethod.getName();
    }
  }

  private Class getClass(ConvertContext context) {
    return DomUtil.getParentOfType(context.getInvocationElement(), Class.class, true);
  }
}
