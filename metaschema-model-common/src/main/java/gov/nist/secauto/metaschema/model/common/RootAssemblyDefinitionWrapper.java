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
package gov.nist.secauto.metaschema.model.common;

import gov.nist.secauto.metaschema.model.common.constraint.IAllowedValuesConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.ICardinalityConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IExpectConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IIndexConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IIndexHasKeyConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IMatchesConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IUniqueConstraint;
import gov.nist.secauto.metaschema.model.common.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.model.common.datatype.markup.MarkupMultiline;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * Wraps an {@link IAssemblyDefinition} that is a {@link IRootAssemblyDefinition}.

 * @param <T> the type of the wrapped definition
 */
public class RootAssemblyDefinitionWrapper<T extends IAssemblyDefinition> implements IRootAssemblyDefinition {
  @NotNull
  private final T rootDefinition;

  public RootAssemblyDefinitionWrapper(@NotNull T rootDefinition) {
    if (!rootDefinition.isRoot()) {
      throw new IllegalArgumentException(
          "Provided definition is not a root assembly: " + rootDefinition.toCoordinates());
    }
    this.rootDefinition = rootDefinition;

  }

  @NotNull
  protected T getRootDefinition() {
    return rootDefinition;
  }

  @Override
  public String getFormalName() {
    return getRootDefinition().getFormalName();
  }

  @Override
  public MarkupLine getDescription() {
    return getRootDefinition().getDescription();
  }

  @Override
  public String getName() {
    return getRootDefinition().getName();
  }

  @Override
  public String getUseName() {
    return getRootDefinition().getUseName();
  }

  @Override
  public MarkupMultiline getRemarks() {
    return getRootDefinition().getRemarks();
  }

  @Override
  public IMetaschema getContainingMetaschema() {
    return getRootDefinition().getContainingMetaschema();
  }

  @Override
  public boolean isRoot() {
    // always true for a root definition
    return true;
  }

  @SuppressWarnings("null")
  @Override
  public String getRootName() {
    return getRootDefinition().getRootName();
  }

  @Override
  public boolean isInline() {
    // always false, since this is a root
    return false;
  }

  @Override
  public IAssemblyInstance getInlineInstance() {
    // always null, since this is a root
    return null;
  }

  @Override
  public ModuleScopeEnum getModuleScope() {
    // always INHERITED, since roots are always inherited
    return ModuleScopeEnum.INHERITED;
  }

  @Override
  public boolean hasJsonKey() {
    // always null, since this is a root
    return false;
  }

  @Override
  public IFlagInstance getJsonKeyFlagInstance() {
    // always null, since this is a root
    return null;
  }


  @Override
  public IFlagInstance getFlagInstanceByName(String name) {
    return getRootDefinition().getFlagInstanceByName(name);
  }

  @Override
  public Collection<@NotNull ? extends IFlagInstance> getFlagInstances() {
    return getRootDefinition().getFlagInstances();
  }

  @Override
  public Collection<@NotNull ? extends IModelInstance> getModelInstances() {
    return getRootDefinition().getModelInstances();
  }

  @Override
  public Collection<@NotNull ? extends INamedModelInstance> getNamedModelInstances() {
    return getRootDefinition().getNamedModelInstances();
  }

  @Override
  public INamedModelInstance getModelInstanceByName(String name) {
    return getRootDefinition().getModelInstanceByName(name);
  }

  @Override
  public Collection<@NotNull ? extends IFieldInstance> getFieldInstances() {
    return getRootDefinition().getFieldInstances();
  }

  @Override
  public IFieldInstance getFieldInstanceByName(String name) {
    return getRootDefinition().getFieldInstanceByName(name);
  }

  @Override
  public Collection<@NotNull ? extends IAssemblyInstance> getAssemblyInstances() {
    return getRootDefinition().getAssemblyInstances();
  }

  @Override
  public IAssemblyInstance getAssemblyInstanceByName(String name) {
    return getRootDefinition().getAssemblyInstanceByName(name);
  }

  @Override
  public List<@NotNull ? extends IChoiceInstance> getChoiceInstances() {
    return getRootDefinition().getChoiceInstances();
  }


  @Override
  public List<@NotNull ? extends IConstraint> getConstraints() {
    return getRootDefinition().getConstraints();
  }

  @Override
  public List<@NotNull ? extends IIndexConstraint> getIndexConstraints() {
    return getRootDefinition().getIndexConstraints();
  }

  @Override
  public List<@NotNull ? extends IUniqueConstraint> getUniqueConstraints() {
    return getRootDefinition().getUniqueConstraints();
  }


  @Override
  public List<@NotNull ? extends ICardinalityConstraint> getHasCardinalityConstraints() {
    return getRootDefinition().getHasCardinalityConstraints();
  }


  @Override
  public List<@NotNull ? extends IAllowedValuesConstraint> getAllowedValuesContraints() {
    return getRootDefinition().getAllowedValuesContraints();
  }

  @Override
  public List<@NotNull ? extends IMatchesConstraint> getMatchesConstraints() {
    return getRootDefinition().getMatchesConstraints();
  }

  @Override
  public List<@NotNull ? extends IIndexHasKeyConstraint> getIndexHasKeyConstraints() {
    return getRootDefinition().getIndexHasKeyConstraints();
  }

  @Override
  public List<@NotNull ? extends IExpectConstraint> getExpectConstraints() {
    return getRootDefinition().getExpectConstraints();
  }

}