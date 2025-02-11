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

import gov.nist.secauto.metaschema.model.common.IAssemblyDefinition;
import gov.nist.secauto.metaschema.model.common.IDefinition;
import gov.nist.secauto.metaschema.model.common.IFieldDefinition;
import gov.nist.secauto.metaschema.model.common.IFlagDefinition;
import gov.nist.secauto.metaschema.model.common.metapath.MetapathExpression;

import java.util.List;
import java.util.Locale;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides an base implementation for a set of external constraints that target a given definition
 * using a target Metapath expression.
 *
 * @param <T>
 *          the type of the constraint container
 */
public abstract class AbstractTargetedConstraints<T extends IValueConstraintSupport> implements ITargetedConstaints {
  @NonNull
  private final MetapathExpression targetExpression;
  @NonNull
  private final T constraints;

  public AbstractTargetedConstraints(@NonNull MetapathExpression targetExpression, @NonNull T constraints) {
    this.targetExpression = targetExpression;
    this.constraints = constraints;
  }

  @Override
  public MetapathExpression getTargetExpression() {
    return targetExpression;
  }

  @NonNull
  protected T getConstraintSupport() {
    return constraints;
  }

  @Override
  public List<? extends IConstraint> getConstraints() {
    return getConstraintSupport().getConstraints();
  }

  @Override
  public List<? extends IAllowedValuesConstraint> getAllowedValuesConstraints() {
    return getConstraintSupport().getAllowedValuesConstraints();
  }

  @Override
  public List<? extends IMatchesConstraint> getMatchesConstraints() {
    return getConstraintSupport().getMatchesConstraints();
  }

  @Override
  public List<? extends IIndexHasKeyConstraint> getIndexHasKeyConstraints() {
    return getConstraintSupport().getIndexHasKeyConstraints();
  }

  @Override
  public List<? extends IExpectConstraint> getExpectConstraints() {
    return getConstraintSupport().getExpectConstraints();
  }

  @Override
  public void addConstraint(@NonNull IAllowedValuesConstraint constraint) {
    getConstraintSupport().addConstraint(constraint);
  }

  @Override
  public void addConstraint(@NonNull IMatchesConstraint constraint) {
    getConstraintSupport().addConstraint(constraint);
  }

  @Override
  public void addConstraint(@NonNull IIndexHasKeyConstraint constraint) {
    getConstraintSupport().addConstraint(constraint);
  }

  @Override
  public void addConstraint(@NonNull IExpectConstraint constraint) {
    getConstraintSupport().addConstraint(constraint);
  }

  @SuppressWarnings("null")
  protected void applyTo(@NonNull IDefinition definition) {
    getAllowedValuesConstraints().forEach(constraint -> definition.addConstraint(constraint));
    getMatchesConstraints().forEach(constraint -> definition.addConstraint(constraint));
    getIndexHasKeyConstraints().forEach(constraint -> definition.addConstraint(constraint));
    getExpectConstraints().forEach(constraint -> definition.addConstraint(constraint));
  }

  @Override
  public void target(@NonNull IFlagDefinition definition) {
    throw new IllegalStateException(
        String.format("The targeted definition '%s' from metaschema '%s' is not a %s definition.",
            definition.getEffectiveName(),
            definition.getContainingMetaschema().getQName().toString(),
            definition.getModelType().name().toLowerCase(Locale.ROOT)));
  }

  @Override
  public void target(@NonNull IFieldDefinition definition) {
    throw new IllegalStateException(
        String.format("The targeted definition '%s' from metaschema '%s' is not a %s definition.",
            definition.getEffectiveName(),
            definition.getContainingMetaschema().getQName().toString(),
            definition.getModelType().name().toLowerCase(Locale.ROOT)));
  }

  @Override
  public void target(@NonNull IAssemblyDefinition definition) {
    throw new IllegalStateException(
        String.format("The targeted definition '%s' from metaschema '%s' is not a %s definition.",
            definition.getEffectiveName(),
            definition.getContainingMetaschema().getQName().toString(),
            definition.getModelType().name().toLowerCase(Locale.ROOT)));
  }
}
