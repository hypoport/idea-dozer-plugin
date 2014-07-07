package de.hypoport.plugins.dozer.model;

import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomElementProblemDescriptor;
import de.hypoport.plugins.dozer.Messages;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldExpression {

  public static final char TERM_SEPARATOR = '.';

  public static final int SUGGESTION_DEPTH = 10;

  private static final Pattern TERM_PATTERN = Pattern.compile("(^|\\G\\.)(\\w+)(?:\\[(\\d+)\\])?");

  private String expression;

  private List<Term> terms = new LinkedList<>();

  private FieldDefinition fieldDefinition;

  private Field field;
  private FieldExclude fieldExclude;
  private Mapping mapping;

  private List<ProblemDescription> expressionProblems = new LinkedList<ProblemDescription>();

  private PsiClass getBaseClass() {
    if (fieldDefinition == null || mapping == null) {
      return null;
    }
    if ("a".equals(fieldDefinition.getXmlElementName())) {
      return mapping.getClassA().getValue();
    }
    else {
      return mapping.getClassB().getValue();
    }
  }

  @SuppressWarnings({"OverlyLongMethod"})
  public FieldExpression(FieldDefinition fieldDefinition, String expression) {
    this.fieldDefinition = fieldDefinition;
    this.expression = expression;

    if (expression.length() == 0) {
      addExpressionProblem(new ProblemDescription(Messages.getMessage("FieldExpression.emptyExpression"),
                                                  HighlightSeverity.ERROR));
    }

    field = DomUtil.getParentOfType(fieldDefinition, Field.class, true);
    if (field == null) {
      fieldExclude = DomUtil.getParentOfType(fieldDefinition, FieldExclude.class, true);
      mapping = DomUtil.getParentOfType(fieldExclude, Mapping.class, true);
    }
    else {
      mapping = DomUtil.getParentOfType(field, Mapping.class, true);
    }

    PsiClass baseClass = getBaseClass();
    // prerequisites failed, no need to go ahead.
    if (mapping == null
        || (field == null && fieldExclude == null)
        || baseClass == null) {
      return;
    }

    Matcher m = TERM_PATTERN.matcher(expression);

    int matchedChars = 0;
    PsiClass actualType = baseClass;

    while (m.find()) {
      if (m.start() == 0
          && (m.group(1).length() != 0)) {
        addExpressionProblem(Messages.getMessage("FieldExpression.unexpectedText", m.group(1)), // $NON-NLS-1$
                             HighlightSeverity.ERROR);
      }

      String element = m.group(2); // the \\w+ part
      String index = m.group(3);   // the \\[\\d+\\] part

      PropertyDescriptor property = findProperty(actualType, element);
      if (property == null) {
        if (!"this".equals(element)) { // suppress "this" as keyword for self-references (i.e. in maps)
          addExpressionProblem(
              Messages.getMessage("FieldExpression.unknownProperty", element, actualType.getName()), // $NON-NLS-1$
              HighlightSeverity.ERROR);
          return;
        }
        else
        {
          // avoid NPE
          return;
        }
      }

      terms.add(new Term(property, index));
      matchedChars += m.group().length();

      if (property.getType() instanceof PsiClassType) {
        actualType = PsiUtil.resolveClassInType(property.getType());

        if (index != null && actualType != null) {

          if (!isOfTypeList(actualType)) {
            addExpressionProblem(
                Messages.getMessage("FieldExpression.nonIndexable", element, actualType.getName()), // $NON-NLS-1$
                HighlightSeverity.ERROR);
          }
          else {
            PsiClassType.ClassResolveResult resolveResult = PsiUtil.resolveGenericsClassInType(property.getType());
            PsiType paramType = resolveResult.getSubstitutor().substitute(actualType.getTypeParameters()[0]);
            PsiClass paramClass = PsiUtil.resolveClassInType(paramType);
            if (paramClass != null) {
              actualType = paramClass;
            }
          }
        }
      }
      else if (property.getType() instanceof PsiArrayType) {
        if (index != null) {
          PsiClass compClass = PsiUtil.resolveClassInType(property.getType());
          if (compClass != null) {
            actualType = compClass;
          }
        }
      }
      else {
        break;
      }
    }

    if (matchedChars != expression.length()) {
      String unmatched = expression.substring(expression.length() - matchedChars);
      addExpressionProblem(Messages.getMessage("FieldExpression.unmatchedText", unmatched), // $NON-NLS-1$
                           HighlightSeverity.ERROR,
                           new TextRange(expression.length() - matchedChars, expression.length()));
    }
  }

  private boolean isOfTypeList(PsiClass actualPsiClass) {
    Project project = actualPsiClass.getProject();
    PsiClass listClazz = JavaPsiFacade.getInstance(project).findClass(List.class.getName(), GlobalSearchScope.allScope(project));

    return isClazzEqual(actualPsiClass, listClazz) || actualPsiClass.isInheritor(listClazz, true);
  }

  private boolean isClazzEqual(PsiClass clazz1, PsiClass clazz2) {
    if (clazz1 == null || clazz2 == null) {
      return clazz1 == clazz2;
    }

    return clazz2.getQualifiedName().equals(clazz1.getQualifiedName());
  }

  private PropertyDescriptor findProperty(PsiClass baseClass, String propertyName) {
    PsiField field = PropertyUtil.findPropertyField(baseClass, propertyName, false);
    PsiMethod getter = PropertyUtil.findPropertyGetter(baseClass, propertyName, false, true);
    PsiMethod setter = PropertyUtil.findPropertySetter(baseClass, propertyName, false, true);
    if (field != null
        || getter != null
        || setter != null) {
      return new PropertyDescriptor(propertyName, field, getter, setter);
    }
    else {
      return null;
    }
  }

  public void annotate(DomElementAnnotationHolder holder) {
    for (ProblemDescription description : expressionProblems) {
      description.toDomProblem(holder);
    }

    checkPropertyAccess(holder);
  }

  private void checkPropertyAccess(DomElementAnnotationHolder holder) {
    ListIterator<Term> termIt = terms.listIterator();
    while (termIt.hasNext()) {
      Term term = termIt.next();
      boolean isLastTerm = !termIt.hasNext();
      PropertyDescriptor property = term.getProperty();

      boolean requireWritable;

      if ("a".equals(fieldDefinition.getXmlElementName())) {
        requireWritable = getMappingType() == Type.BI_DIRECTIONAL && isLastTerm;
      }
      else {
        requireWritable = isLastTerm;
      }

      if (!property.isReadable()) {
        holder.createProblem(fieldDefinition,
                             HighlightSeverity.ERROR,
                             Messages.getMessage("FieldExpression.unreadable", property.getPropertyName())); //$NON-NLS-1$
      }
      if (requireWritable && !property.isWritable()) {
        holder.createProblem(fieldDefinition,
                             HighlightSeverity.ERROR,
                             Messages.getMessage("FieldExpression.unwritable", property.getPropertyName())); //$NON-NLS-1$
      }
    }
  }

  private void addExpressionProblem(String message, HighlightSeverity severity) {
    addExpressionProblem(new ProblemDescription(message, severity));
  }

  private void addExpressionProblem(String message, HighlightSeverity severity, TextRange textRange) {
    addExpressionProblem(new ProblemDescription(message, severity, textRange));
  }

  private void addExpressionProblem(ProblemDescription problem) {
    expressionProblems.add(problem);
  }

  private boolean isFieldAccessible() {
    return Boolean.TRUE.equals(fieldDefinition.getIsAccessible().getValue());
  }

  @NotNull
  private Type getMappingType() {
    Type result;
    if (field != null) {
      result = field.getType().getValue();
    }
    else {
      result = fieldExclude.getType().getValue();
    }

    if (result == null) {
      result = mapping.getType().getValue();
    }

    if (result == null) {
      return Type.BI_DIRECTIONAL;
    }
    else {
      return result;
    }
  }

  private List<FieldExpression> suggestVariants(PsiClass extensionBase, String expression, int depth) {
    if (depth < 1) {
      return Collections.emptyList();
    }
    if (extensionBase == null || extensionBase.getQualifiedName() == null || !extensionBase.getQualifiedName().startsWith("de.hypoport")) {
      // TODO ASC Sprint31 - nur für unterstützte Typen Auswahl erweitern
      return Collections.emptyList();
    }

    if (isFieldAccessible()) {
      return suggestVariantsForMembers(expression, depth, findAllNonStaticAndNonFinalFields(extensionBase));
    }
    else {
      return suggestVariantsForMembers(expression, depth, findProperties(extensionBase));
    }
  }

  private List<PsiNameAndType> findAllNonStaticAndNonFinalFields(PsiClass psiClass) {
    List<PsiNameAndType> result = new ArrayList<PsiNameAndType>();
    for (PsiField field : psiClass.getAllFields()) {
      if (!field.hasModifierProperty(PsiModifier.STATIC)
          && !field.hasModifierProperty(PsiModifier.FINAL)) {
        result.add(new PsiNameAndType(field));
      }
    }
    return result;
  }

  private List<PsiNameAndType> findProperties(PsiClass extensionBase) {
    Map<String, PsiMethod> getters = PropertyUtil.getAllProperties(extensionBase, false, true, true);

    List<PsiNameAndType> result = new ArrayList<PsiNameAndType>();
    for (PsiMethod method : getters.values()) {
      result.add(new PsiNameAndType(method));
    }
    return result;
  }

  private List<FieldExpression> suggestVariantsForMembers(String parentExpression, int depth, List<PsiNameAndType> members) {
    List<FieldExpression> result = new LinkedList<FieldExpression>();
    for (PsiNameAndType member : members) {
      String prefix = parentExpression == null ? "" : parentExpression + TERM_SEPARATOR;
      String expression = prefix + member.getName();
      result.add(new FieldExpression(fieldDefinition, expression));
      result.addAll(suggestVariantsForCollectionMembers(member.getType(), depth, expression));
    }
    return result;
  }

  private List<FieldExpression> suggestVariantsForCollectionMembers(PsiType nextExtensionBaseType, int depth, String nexpr) {
    List<FieldExpression> result = new LinkedList<FieldExpression>();
    PsiClass nextExtensionBase = PsiUtil.resolveClassInType(nextExtensionBaseType);
    if (nextExtensionBase != null) {
      result.addAll(suggestVariants(nextExtensionBase, nexpr, depth - 1));

      final String nexprComp = nexpr + "[0]";
      if (nextExtensionBaseType instanceof PsiArrayType) {
        result.add(new FieldExpression(fieldDefinition, nexprComp));

        PsiType compType = ((PsiArrayType) nextExtensionBaseType).getComponentType();
        PsiClass compClass = PsiUtil.resolveClassInType(compType);
        if (compClass != null) {
          result.addAll(suggestVariants(compClass, nexprComp, depth - 1));
        }
      }
      else if (isOfTypeList(nextExtensionBase)) {
        PsiClassType listClassType = (PsiClassType) nextExtensionBaseType;
        if (listClassType.hasParameters()) {
          PsiClassType.ClassResolveResult resolveResult = listClassType.resolveGenerics();
          PsiType paramType = resolveResult.getSubstitutor().substitute(listClassType.getParameters()[0]);
          if (paramType instanceof PsiClassType) {
            PsiClass paramClass = PsiUtil.resolveClassInType(paramType);
            result.addAll(suggestVariants(paramClass, nexprComp, depth - 1));
          }
        }
        result.add(new FieldExpression(fieldDefinition, nexprComp));
      }
    }
    return result;
  }

  public List<FieldExpression> suggestVariants() {
    PsiClass baseClass = getBaseClass();

    if (baseClass == null) {
      return Collections.emptyList();
    }
    else {
      return suggestVariants(baseClass, null, SUGGESTION_DEPTH);
    }
  }

  @Override
  public String toString() {
    return expression;
  }

  private class ProblemDescription {

    private String message;
    private HighlightSeverity severity;
    private TextRange textRange;

    private ProblemDescription(String message, HighlightSeverity severity) {
      this(message, severity, null);
    }

    private ProblemDescription(String message, HighlightSeverity severity, TextRange textRange) {
      this.message = message;
      this.severity = severity;
      this.textRange = textRange;
    }

    public DomElementProblemDescriptor toDomProblem(DomElementAnnotationHolder holder) {
      if (textRange == null) {
        return holder.createProblem(FieldExpression.this.fieldDefinition, severity, message);
      }
      else {
        return holder.createProblem(FieldExpression.this.fieldDefinition, severity, message, textRange);
      }
    }
  }

  private class Term {

    private String term;
    private PropertyDescriptor property;
    private String index;

    private Term(PropertyDescriptor property, String index) {
      this.property = property;
      this.index = index;
    }

    public PropertyDescriptor getProperty() {
      return property;
    }

    @Override
    public String toString() {
      if (term == null) {
        if (index == null) {
          term = property.getPropertyName();
        }
        else {
          term = property.getPropertyName() + '[' + index + ']';
        }
      }
      return term;
    }
  }

  private class PropertyDescriptor {

    private String propertyName;
    private PsiField field;
    private PsiMethod getter;
    private PsiMethod setter;

    private PsiType type;

    public PropertyDescriptor(String propertyName, PsiField field, PsiMethod getter, PsiMethod setter) {
      this.propertyName = propertyName;
      this.field = field;
      this.getter = getter;
      this.setter = setter;

      if (field != null) {
        type = field.getType();
      }
      if (getter != null) {
        type = getter.getReturnType();
      }
      if (setter != null && type == null) {
        type = setter.getParameterList().getParameters()[0].getType();
      }
    }

    public boolean isReadable() {
      return hasGetter()
             || (hasField() && isFieldAccessible());
    }

    public boolean isWritable() {
      return hasSetter()
             || (hasField() && isFieldAccessible());
    }

    public PsiType getType() {
      return type;
    }

    public boolean hasField() {
      return field != null;
    }

    public boolean hasGetter() {
      return getter != null;
    }

    public boolean hasSetter() {
      return setter != null;
    }

    public String getPropertyName() {
      return propertyName;
    }
  }
}
