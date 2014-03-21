package de.hypoport.plugins.dozer.model;

import com.intellij.util.xml.NamedEnum;

public enum RelationshipType implements NamedEnum {

  CUMULATIVE("cumulative"), //$NON-NLS-1$
  NON_CUMULATIVE("non-cumulative"); //$NON-NLS-1$

  private final String value;

  RelationshipType(String value) {
    this.value = value;
  }

  @Override
  public String getValue() {
    return value;
  }

}
