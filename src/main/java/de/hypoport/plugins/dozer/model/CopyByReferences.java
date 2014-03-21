package de.hypoport.plugins.dozer.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CopyByReferences extends DozerDomElement {

  @NotNull
  List<CopyByReference> getCopyByReferences();

  CopyByReference addCopyByReference();
}
