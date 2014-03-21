package de.hypoport.plugins.dozer.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Variables extends DozerDomElement {

  @NotNull
  List<Variable> getVariables();

  Variable addVariable();
}
