package de.hypoport.plugins.dozer.model;

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import org.apache.commons.lang.StringUtils;

public class PsiNameAndType {

  private String name;
  private PsiType type;

  public PsiNameAndType(PsiField field) {
    this.name = field.getName();
    this.type = field.getType();
  }

  public PsiNameAndType(PsiMethod method) {
    this.name = calculateName(method.getName());
    this.type = method.getReturnType();
  }

  private String calculateName(String methodName) {
    if (methodName.startsWith("get")) {
      return StringUtils.uncapitalize(methodName.replaceFirst("get", ""));
    }
    if (methodName.startsWith("is")) {
      return StringUtils.uncapitalize(methodName.replaceFirst("is", ""));
    }
    return methodName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public PsiType getType() {
    return type;
  }

  public void setType(PsiType type) {
    this.type = type;
  }
}
