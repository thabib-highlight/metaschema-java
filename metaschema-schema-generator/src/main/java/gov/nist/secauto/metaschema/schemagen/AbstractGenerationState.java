/*
 * Portions of this software was developed by employees of the National Institute
 * of Standards and Technology (NIST), an agency of the Federal Government and is
 * being made available as a public service. Pursuant to title 17 United States
 * Code Section 105, works of NIST employees are not subject to copyright
 * protection in the United States. This software may be subject to foreign
 * copyright. Permission in the United States and in foreign countries, to the
 * extent that NIST may hold copyright, to use, copy, modify, create derivative
 * works, and distribute this software and its documentation without fee is hereby
 * granted on a non-exclusive basis, provided that this notice and disclaimer
 * of warranty appears in all copies.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS' WITHOUT ANY WARRANTY OF ANY KIND, EITHER
 * EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY WARRANTY
 * THAT THE SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND FREEDOM FROM
 * INFRINGEMENT, AND ANY WARRANTY THAT THE DOCUMENTATION WILL CONFORM TO THE
 * SOFTWARE, OR ANY WARRANTY THAT THE SOFTWARE WILL BE ERROR FREE.  IN NO EVENT
 * SHALL NIST BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT LIMITED TO, DIRECT,
 * INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES, ARISING OUT OF, RESULTING FROM,
 * OR IN ANY WAY CONNECTED WITH THIS SOFTWARE, WHETHER OR NOT BASED UPON WARRANTY,
 * CONTRACT, TORT, OR OTHERWISE, WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR
 * PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM, OR AROSE OUT
 * OF THE RESULTS OF, OR USE OF, THE SOFTWARE OR SERVICES PROVIDED HEREUNDER.
 */

package gov.nist.secauto.metaschema.schemagen;

import gov.nist.secauto.metaschema.model.common.IDefinition;
import gov.nist.secauto.metaschema.model.common.IMetaschema;
import gov.nist.secauto.metaschema.model.common.INamedInstance;
import gov.nist.secauto.metaschema.model.common.IValuedDefinition;
import gov.nist.secauto.metaschema.model.common.configuration.IConfiguration;
import gov.nist.secauto.metaschema.model.common.constraint.IAllowedValue;
import gov.nist.secauto.metaschema.model.common.constraint.IAllowedValuesConstraint;
import gov.nist.secauto.metaschema.model.common.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.model.common.util.CollectionUtil;
import gov.nist.secauto.metaschema.model.common.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.datatype.IDatatypeManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public abstract class AbstractGenerationState<WRITER, DATATYPE_MANAGER extends IDatatypeManager>
    implements IGenerationState<WRITER> {
  @NonNull
  private final IMetaschema metaschema;
  @NonNull
  private final WRITER writer;
  @NonNull
  private final DATATYPE_MANAGER datatypeManager;
  @NonNull
  private final IInlineStrategy inlineStrategy;

  @NonNull
  private final MetaschemaIndex metaschemaIndex;

  public AbstractGenerationState(
      @NonNull IMetaschema metaschema,
      @NonNull WRITER writer,
      @NonNull IConfiguration<SchemaGenerationFeature<?>> configuration,
      @NonNull DATATYPE_MANAGER datatypeManager) {
    this.metaschema = metaschema;
    this.writer = writer;
    this.datatypeManager = datatypeManager;
    this.inlineStrategy = IInlineStrategy.newInlineStrategy(configuration);
    this.metaschemaIndex = MetaschemaIndex.indexDefinitions(metaschema);
  }

  @Override
  public IMetaschema getMetaschema() {
    return metaschema;
  }

  @Override
  public WRITER getWriter() {
    return writer;
  }

  @NonNull
  protected DATATYPE_MANAGER getDatatypeManager() {
    return datatypeManager;
  }

  @NonNull
  public MetaschemaIndex getMetaschemaIndex() {
    return metaschemaIndex;
  }

  @Override
  public boolean isInline(@NonNull IDefinition definition) {
    return inlineStrategy.isInline(definition, getMetaschemaIndex());
  }

  /**
   * Retrieve any allowed values that are context independent, meaning they always apply regardless of
   * the location of the node in the larger graph.
   *
   * @param definition
   *          the definition to get allowed values for
   * @return the list of allowed values or an empty list
   */
  @NonNull
  protected static AllowedValueCollection getContextIndependentEnumeratedValues(
      @NonNull IValuedDefinition definition) {
    List<IAllowedValue> values = new LinkedList<>();
    boolean closed = false;
    for (IAllowedValuesConstraint constraint : definition.getAllowedValuesConstraints()) {
      if (!constraint.isAllowedOther()) {
        closed = true;
      }

      if (!MetapathExpression.CONTEXT_NODE.equals(constraint.getTarget())) {
        values = CollectionUtil.emptyList();
        break;
      }

      values.addAll(constraint.getAllowedValues().values());
    }
    return new AllowedValueCollection(closed, values);
  }

  /**
   * Get the name of the definition (and any parent instances/definition) to ensure an inline type is
   * unique.
   *
   * @param definition
   *          the definition to generate a type name for
   * @param childMetaschema
   *          the metaschema of the left node
   * @return the unique type name
   */
  private CharSequence getTypeContext(
      @NonNull IDefinition definition,
      @NonNull IMetaschema childMetaschema) {
    StringBuilder builder = new StringBuilder();
    if (definition.isInline()) {
      INamedInstance inlineInstance = definition.getInlineInstance();
      IDefinition parentDefinition = inlineInstance.getContainingDefinition();

      builder
          .append(getTypeContext(parentDefinition, childMetaschema))
          .append(toCamelCase(inlineInstance.getEffectiveName()));
    } else {
      builder.append(toCamelCase(definition.getEffectiveName()));
    }
    return builder;
  }

  @NonNull
  public String getTypeNameForDefinition(@NonNull IDefinition definition, @Nullable String suffix) {
    StringBuilder builder = new StringBuilder()
        .append(toCamelCase(definition.getModelType().name()))
        .append(toCamelCase(definition.getContainingMetaschema().getShortName()));

    if (isInline(definition)) {
      builder.append(toCamelCase(definition.getEffectiveName()));
    } else {
      // need to append the parent name(s) to disambiguate this type name
      builder.append(getTypeContext(definition, definition.getContainingMetaschema()));
    }
    if (suffix != null && !suffix.isBlank()) {
      builder.append(toCamelCase(suffix));
    }
    builder.append("Type");

    return ObjectUtils.notNull(builder.toString());
  }

  @NonNull
  protected static CharSequence toCamelCase(String text) {
    StringBuilder builder = new StringBuilder();
    for (String segment : text.split("\\p{Punct}")) {
      if (segment.length() > 0) {
        builder.append(segment.substring(0, 1).toUpperCase(Locale.ROOT));
      }
      if (segment.length() > 1) {
        builder.append(segment.substring(1).toLowerCase(Locale.ROOT));
      }
    }
    return builder;
  }

  public static class AllowedValueCollection {
    private final boolean closed;
    @NonNull
    private final List<IAllowedValue> values;

    public AllowedValueCollection(boolean closed, @NonNull List<IAllowedValue> values) {
      this.closed = closed;
      this.values = CollectionUtil.unmodifiableList(new ArrayList<>(values));
    }

    public boolean isClosed() {
      return closed;
    }

    @NonNull
    public List<IAllowedValue> getValues() {
      return values;
    }
  }
}
