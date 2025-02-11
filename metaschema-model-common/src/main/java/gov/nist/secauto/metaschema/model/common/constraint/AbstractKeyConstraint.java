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

package gov.nist.secauto.metaschema.model.common.constraint;

import gov.nist.secauto.metaschema.model.common.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.model.common.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.model.common.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.model.common.util.CollectionUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public abstract class AbstractKeyConstraint
    extends AbstractConstraint
    implements IKeyConstraint {
  @NonNull
  private final List<DefaultKeyField> keyFields;

  /**
   * Create a new key-based constraint, which uses a set of key fields to build a key.
   *
   * @param id
   *          the optional identifier for the constraint
   * @param formalName
   *          the constraint's formal name or {@code null} if not provided
   * @param description
   *          the constraint's semantic description or {@code null} if not provided
   * @param source
   *          information about the constraint source
   * @param level
   *          the significance of a violation of this constraint
   * @param target
   *          the Metapath expression identifying the nodes the constraint targets
   * @param properties
   *          a collection of associated properties
   * @param keyFields
   *          a list of key fields associated with the constraint
   * @param remarks
   *          optional remarks describing the intent of the constraint
   */
  protected AbstractKeyConstraint(
      @Nullable String id,
      @Nullable String formalName,
      @Nullable MarkupLine description,
      @NonNull ISource source,
      @NonNull Level level,
      @NonNull MetapathExpression target,
      @NonNull Map<QName, Set<String>> properties,
      @NonNull List<DefaultKeyField> keyFields,
      @Nullable MarkupMultiline remarks) {
    super(id, formalName, description, source, level, target, properties, remarks);
    if (keyFields.isEmpty()) {
      throw new IllegalArgumentException("an empty list of key fields is not allowed");
    }
    this.keyFields = keyFields;
  }

  @Override
  public List<DefaultKeyField> getKeyFields() {
    return keyFields;
  }

  public abstract static class AbstractKeyConstraintBuilder<T extends AbstractKeyConstraintBuilder<T,
      R>, R extends AbstractKeyConstraint>
      extends AbstractConstraintBuilder<T, R> {
    @NonNull
    private final List<DefaultKeyField> keyFields = new LinkedList<>();

    public T keyField(@NonNull DefaultKeyField keyField) {
      this.keyFields.add(keyField);
      return getThis();
    }

    @NonNull
    protected List<DefaultKeyField> getKeyFields() {
      return keyFields;
    }

    @Override
    protected void validate() {
      super.validate();

      CollectionUtil.requireNonEmpty(getKeyFields());
    }
  }
}
