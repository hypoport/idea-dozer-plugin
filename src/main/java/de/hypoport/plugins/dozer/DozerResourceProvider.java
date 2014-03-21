package de.hypoport.plugins.dozer;

import com.intellij.javaee.ResourceRegistrar;
import com.intellij.javaee.StandardResourceProvider;

public class DozerResourceProvider implements StandardResourceProvider {

  @Override
  public void registerResources(ResourceRegistrar registrar) {
    registrar.addStdResource(DozerConstants.NAMESPACE_SCHEMA_LOCATION, "beanmapping.xsd", getClass()); //NON-NLS-1
  }
}
