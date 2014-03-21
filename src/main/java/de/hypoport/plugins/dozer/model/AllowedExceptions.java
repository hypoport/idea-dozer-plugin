package de.hypoport.plugins.dozer.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface AllowedExceptions extends DozerDomElement {

  @NotNull
  List<Exception> getExceptions();

  Exception addException();
}
