package de.hypoport.plugins.dozer.model;

import com.intellij.util.xml.NamedEnum;

public enum Type implements NamedEnum {

  ONE_WAY("one-way"), //$NON-NLS-1$
  BI_DIRECTIONAL("bi-directional"); //$NON-NLS-1$

  private final String value;

  Type(String value) {
    this.value = value;
  }

  @Override
  public String getValue() {
    return value;
  }
}
