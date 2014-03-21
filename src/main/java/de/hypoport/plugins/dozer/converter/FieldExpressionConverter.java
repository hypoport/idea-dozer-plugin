package de.hypoport.plugins.dozer.converter;

import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.ResolvingConverter;
import de.hypoport.plugins.dozer.model.FieldDefinition;
import de.hypoport.plugins.dozer.model.FieldExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public class FieldExpressionConverter extends ResolvingConverter<FieldExpression> {

  @NotNull
  @Override
  public Collection<? extends FieldExpression> getVariants(ConvertContext context) {
    FieldDefinition fieldDefinition = DomUtil.getParentOfType(context.getInvocationElement(), FieldDefinition.class, false);
    FieldExpression expression = fieldDefinition.getValue();
    if (expression != null) {
      return expression.suggestVariants();
    }
    else {
      return Collections.emptyList();
    }
  }

  @Override
  public FieldExpression fromString(@Nullable String s, ConvertContext context) {
    return parseExpression(s, context);
  }

  @Override
  public String toString(@Nullable FieldExpression fieldExpression, ConvertContext context) {
    return fieldExpression == null ? null : fieldExpression.toString();
  }

  private FieldExpression parseExpression(String expr, ConvertContext context) {
    FieldDefinition fieldDefinition = DomUtil.getParentOfType(context.getInvocationElement(), FieldDefinition.class, false);
    return new FieldExpression(fieldDefinition, expr);
  }
}
