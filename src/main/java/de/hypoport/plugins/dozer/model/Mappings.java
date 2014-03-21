package de.hypoport.plugins.dozer.model;

import com.intellij.util.xml.HyphenNameStrategy;
import com.intellij.util.xml.NameStrategy;
import com.intellij.util.xml.Namespace;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Namespace("http://dozer.sourceforge.net")
@NameStrategy(HyphenNameStrategy.class)
public interface Mappings extends DozerDomElement {

  Configuration getConfiguration();

  @NotNull
  List<Mapping> getMappings();

  Mapping addMapping();
}
