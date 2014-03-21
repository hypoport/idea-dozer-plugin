package de.hypoport.plugins.dozer;

import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileDescription;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomElementsAnnotator;
import de.hypoport.plugins.dozer.model.BeanFactory;
import de.hypoport.plugins.dozer.model.Converter;
import de.hypoport.plugins.dozer.model.FieldDefinition;
import de.hypoport.plugins.dozer.model.FieldExpression;
import de.hypoport.plugins.dozer.model.Mappings;

public class DozerFileDescription extends DomFileDescription<Mappings> {

  public DozerFileDescription() {
    super(Mappings.class, "mappings", DozerConstants.NAMESPACE_URI); //$NON-NLS-1$
  }

  @Override
  protected void initializeFileDescription() {
    registerNamespacePolicy(DozerConstants.NAMESPACE_URI, DozerConstants.NAMESPACE_URI);
  }

  @Override
  public DomElementsAnnotator createAnnotator() {
    return new DozerAnnotator();
  }

  private static class DozerAnnotator implements DomElementsAnnotator {

    @Override
    public void annotate(DomElement element, DomElementAnnotationHolder holder) {
      if (element instanceof FieldDefinition) {
        FieldDefinition definition = (FieldDefinition) element;
        FieldExpression expression = definition.getValue();
        if (expression != null) {
          expression.annotate(holder);
        }
      }
      else if (element instanceof de.hypoport.plugins.dozer.model.Class) {
        de.hypoport.plugins.dozer.model.Class clazz = (de.hypoport.plugins.dozer.model.Class) element;
        if (clazz.getValue() == null
            && clazz.getStringValue() != null
            && clazz.getStringValue().length() != 0) {
          holder.createProblem(clazz,
                               HighlightSeverity.ERROR,
                               Messages.getMessage("Annotator.unresolvedClass", clazz.getStringValue())); //$NON-NLS-1$
        }
      }
      else if (element instanceof de.hypoport.plugins.dozer.model.Exception) {
        de.hypoport.plugins.dozer.model.Exception exception = (de.hypoport.plugins.dozer.model.Exception) element;
        if (exception.getValue() == null) {
          holder.createProblem(exception,
                               HighlightSeverity.ERROR,
                               Messages.getMessage("Annotator.unexpectedType", exception.getStringValue(), "java.lang.Exception")); //$NON-NLS-1$  //$NON-NLS-3$
        }
      }
      else if (element instanceof Converter) {
        Converter converter = (Converter) element;
        if (converter.getType() == null) {
          holder.createProblem(converter,
                               HighlightSeverity.ERROR,
                               Messages.getMessage("Annotator.unexpectedType", converter.getType().getStringValue(), DozerConstants.BEAN_FACTORY_CLASSNAME));  //$NON-NLS-1$
        }
      }
      else if (element instanceof BeanFactory) {
        BeanFactory beanFactory = (BeanFactory) element;
        if (beanFactory.getValue() == null
            && beanFactory.getStringValue() != null
            && beanFactory.getStringValue().length() != 0) {
          holder.createProblem(beanFactory,
                               HighlightSeverity.ERROR,
                               Messages.getMessage("Annotator.unexpectedType", beanFactory.getStringValue(), DozerConstants.BEAN_FACTORY_CLASSNAME));  //$NON-NLS-1$
        }
      }
    }
  }
}
