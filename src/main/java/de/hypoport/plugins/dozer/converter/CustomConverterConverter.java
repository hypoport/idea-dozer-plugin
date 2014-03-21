package de.hypoport.plugins.dozer.converter;

import de.hypoport.plugins.dozer.DozerConstants;

public class CustomConverterConverter extends ClassInheritorConverter {

  public CustomConverterConverter() {
    super(DozerConstants.CUSTOM_CONVERTER_CLASSNAME, CONCRETE_CLASSES_ONLY);
  }
}
