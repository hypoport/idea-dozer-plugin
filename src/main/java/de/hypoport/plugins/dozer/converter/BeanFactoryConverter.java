package de.hypoport.plugins.dozer.converter;

import de.hypoport.plugins.dozer.DozerConstants;

public class BeanFactoryConverter extends ClassInheritorConverter {

  public BeanFactoryConverter() {
    super(DozerConstants.BEAN_FACTORY_CLASSNAME, CONCRETE_CLASSES_ONLY);
  }
}
