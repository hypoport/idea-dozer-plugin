package de.hypoport.plugins.dozer;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.xml.DomUtil;
import de.hypoport.plugins.dozer.model.Class;
import de.hypoport.plugins.dozer.model.FieldDefinition;
import de.hypoport.plugins.dozer.model.Mapping;
import org.jetbrains.annotations.NotNull;

public final class PsiUtil {

  private PsiUtil() {
  }

  /**
   * Returns a PsiClass for class {@link java.lang.Exception}.
   *
   * @param project the project to resolve java.lang.Exception.
   *
   * @return
   */
  @NotNull
  public static PsiClass findException(Project project) {
    return findClass(project, "java.lang.Exception"); //$NON-NLS-1$
  }

  public static PsiClass findBeanFactory(Project project) {
    return findClass(project, "org.dozer.BeanFactory"); //$NON-NLS-1$
  }

  /**
   * Returns a PsiClass by its fully qualified class-name.
   *
   * @param project
   * @param className
   *
   * @return
   */
  public static PsiClass findClass(Project project, String className) {
    final JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
    return psiFacade.findClass(className, GlobalSearchScope.allScope(project));
  }

  /**
   * Returns the related class-X-element for a field-definition.
   *
   * @param fieldDefinition
   *
   * @return
   */
  public static Class findClassElementForField(FieldDefinition fieldDefinition) {
    Mapping mapping = DomUtil.getParentOfType(fieldDefinition, Mapping.class, true);

    if (mapping == null) {
      return null;
    }

    if (fieldDefinition.getXmlTag().getLocalName().equals("a")) { //$NON-NLS-1$
      return mapping.getClassA();
    }
    else {
      return mapping.getClassB();
    }
  }
}
