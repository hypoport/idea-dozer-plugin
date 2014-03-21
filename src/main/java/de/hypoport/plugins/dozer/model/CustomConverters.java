package de.hypoport.plugins.dozer.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CustomConverters extends DozerDomElement {

  @NotNull
  List<Converter> getConverters();

  Converter addConverter();
}
