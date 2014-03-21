package de.hypoport.plugins.dozer.model;

import com.intellij.util.xml.NamedEnum;

public enum FieldType implements NamedEnum {

  ITERATE("iterate"), //$NON-NLS-1$
  GENERIC("generic"); //$NON-NLS-1$

  private final String value;

  FieldType(String value) {
    this.value = value;
  }

  @Override
  public String getValue() {
    return value;
  }

}
