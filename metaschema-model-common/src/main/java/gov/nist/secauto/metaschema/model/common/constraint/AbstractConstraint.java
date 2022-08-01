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

import gov.nist.secauto.metaschema.model.common.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.model.common.metapath.MetapathExpression;

import java.util.Objects;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

abstract class AbstractConstraint implements IConstraint { // NOPMD - intentional data class
  @Nullable
  private final String id;
  @Nullable
  private final ISource source;
  @NonNull
  private final Level level;
  @NonNull
  private final MetapathExpression target;
  @Nullable
  private final MarkupMultiline remarks;

  /**
   * Construct a new Metaschema constraint.
   * 
   * @param id
   *          the optional identifier for the constraint
   * @param source
   *          information about the constraint source
   * @param level
   *          the significance of a violation of this constraint
   * @param target
   *          the Metapath expression identifying the nodes the constraint targets
   * @param remarks
   *          optional remarks describing the intent of the constraint
   */
  @SuppressWarnings("null")
  public AbstractConstraint(
      @Nullable String id,
      @NonNull ISource source,
      @NonNull Level level,
      @NonNull MetapathExpression target,
      @Nullable MarkupMultiline remarks) {
    Objects.requireNonNull(target);
    this.id = id;
    this.source = source;
    this.level = Objects.requireNonNull(level, "level");
    this.target = Objects.requireNonNull(target, "target");
    this.remarks = remarks;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public ISource getSource() {
    return source;
  }

  @Override
  @NonNull
  public Level getLevel() {
    return level;
  }

  @Override
  public MetapathExpression getTarget() {
    return target;
  }

  @Override
  public MarkupMultiline getRemarks() {
    return remarks;
  }
}
