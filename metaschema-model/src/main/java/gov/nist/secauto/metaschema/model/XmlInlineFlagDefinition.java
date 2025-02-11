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

package gov.nist.secauto.metaschema.model;

import gov.nist.secauto.metaschema.model.common.AbstractFlagInstance;
import gov.nist.secauto.metaschema.model.common.IFlagContainer;
import gov.nist.secauto.metaschema.model.common.IFlagDefinition;
import gov.nist.secauto.metaschema.model.common.IInlineDefinition;
import gov.nist.secauto.metaschema.model.common.IMetaschema;
import gov.nist.secauto.metaschema.model.common.MetaschemaModelConstants;
import gov.nist.secauto.metaschema.model.common.ModuleScopeEnum;
import gov.nist.secauto.metaschema.model.common.constraint.IAllowedValuesConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IConstraint.ExternalModelSource;
import gov.nist.secauto.metaschema.model.common.constraint.IExpectConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IIndexHasKeyConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IMatchesConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IValueConstraintSupport;
import gov.nist.secauto.metaschema.model.common.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.model.common.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.model.common.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.model.common.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.model.common.util.CollectionUtil;
import gov.nist.secauto.metaschema.model.common.util.ObjectUtils;
import gov.nist.secauto.metaschema.model.xmlbeans.InlineFlagDefinitionType;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

class XmlInlineFlagDefinition
    extends AbstractFlagInstance {
  @NonNull
  private final InlineFlagDefinitionType xmlFlag;
  @NonNull
  private final InternalFlagDefinition flagDefinition;
  private IValueConstraintSupport constraints;

  /**
   * Constructs a new Metaschema flag definition from an XML representation bound to Java objects.
   *
   * @param xmlFlag
   *          the XML representation bound to Java objects
   * @param parent
   *          the parent definition, which must be a definition type that can contain flags.
   */
  public XmlInlineFlagDefinition(@NonNull InlineFlagDefinitionType xmlFlag, @NonNull IFlagContainer parent) {
    super(parent);
    this.xmlFlag = xmlFlag;
    this.flagDefinition = new InternalFlagDefinition();
  }

  /**
   * Used to generate the instances for the constraints in a lazy fashion when the constraints are
   * first accessed.
   */
  protected void checkModelConstraints() {
    synchronized (this) {
      if (constraints == null) {
        if (getXmlFlag().isSetConstraint()) {
          constraints = new ValueConstraintSupport(
              ObjectUtils.notNull(getXmlFlag().getConstraint()),
              ExternalModelSource.instance(
                  ObjectUtils.requireNonNull(getContainingMetaschema().getLocation())));
        } else {
          constraints = new ValueConstraintSupport();
        }
      }
    }
  }

  /**
   * Get the underlying XML model.
   *
   * @return the XML model
   */
  protected final InlineFlagDefinitionType getXmlFlag() {
    return xmlFlag;
  }

  @Override
  public InternalFlagDefinition getDefinition() {
    return flagDefinition;
  }

  @Override
  public IMetaschema getContainingMetaschema() {
    return getContainingDefinition().getContainingMetaschema();
  }

  @Override
  public String getFormalName() {
    return getXmlFlag().isSetFormalName() ? getXmlFlag().getFormalName() : null;
  }

  @SuppressWarnings("null")
  @Override
  public MarkupLine getDescription() {
    return getXmlFlag().isSetDescription() ? MarkupStringConverter.toMarkupString(getXmlFlag().getDescription())
        : null;
  }

  @Override
  public Map<QName, Set<String>> getProperties() {
    return ModelFactory.toProperties(CollectionUtil.listOrEmpty(getXmlFlag().getPropList()));
  }

  @SuppressWarnings("null")
  @Override
  public String getName() {
    return getXmlFlag().getName();
  }

  @Override
  public String getUseName() {
    return null;
  }

  @Override
  public String getXmlNamespace() {
    return null;
  }

  @Override
  public boolean isRequired() {
    return getXmlFlag().isSetRequired() ? getXmlFlag().getRequired() : MetaschemaModelConstants.DEFAULT_FLAG_REQUIRED;
  }

  @SuppressWarnings("null")
  @Override
  public MarkupMultiline getRemarks() {
    return getXmlFlag().isSetRemarks() ? MarkupStringConverter.toMarkupString(getXmlFlag().getRemarks()) : null;
  }

  @Override
  public Object getValue(@NonNull Object parentValue) {
    // there is no value
    return null;
  }

  /**
   * The corresponding definition for the local flag instance.
   */
  private final class InternalFlagDefinition implements IFlagDefinition, IInlineDefinition<XmlInlineFlagDefinition> {
    @Nullable
    private final Object defaultValue;

    private InternalFlagDefinition() {
      Object defaultValue = null;
      if (getXmlFlag().isSetDefault()) {
        defaultValue = getJavaTypeAdapter().parse(ObjectUtils.requireNonNull(getXmlFlag().getDefault()));
      }
      this.defaultValue = defaultValue;
    }

    @Override
    public Object getDefaultValue() {
      return defaultValue;
    }

    @Override
    public boolean isInline() {
      return true;
    }

    @Override
    public XmlInlineFlagDefinition getInlineInstance() {
      return XmlInlineFlagDefinition.this;
    }

    @Override
    public String getName() {
      return XmlInlineFlagDefinition.this.getName();
    }

    @Override
    public String getUseName() {
      // always use the name
      return null;
    }

    @Override
    public ModuleScopeEnum getModuleScope() {
      return ModuleScopeEnum.LOCAL;
    }

    @Override
    public String getFormalName() {
      return XmlInlineFlagDefinition.this.getFormalName();
    }

    @Override
    public MarkupLine getDescription() {
      return XmlInlineFlagDefinition.this.getDescription();
    }

    @Override
    public Map<QName, Set<String>> getProperties() {
      return XmlInlineFlagDefinition.this.getProperties();
    }

    @SuppressWarnings("null")
    @Override
    public IDataTypeAdapter<?> getJavaTypeAdapter() {
      return getXmlFlag().isSetAsType() ? getXmlFlag().getAsType() : MetaschemaDataTypeProvider.DEFAULT_DATA_TYPE;
    }

    @Override
    public List<? extends IConstraint> getConstraints() {
      checkModelConstraints();
      return constraints.getConstraints();
    }

    @Override
    public List<? extends IAllowedValuesConstraint> getAllowedValuesConstraints() {
      checkModelConstraints();
      return constraints.getAllowedValuesConstraints();
    }

    @Override
    public List<? extends IMatchesConstraint> getMatchesConstraints() {
      checkModelConstraints();
      return constraints.getMatchesConstraints();
    }

    @Override
    public List<? extends IIndexHasKeyConstraint> getIndexHasKeyConstraints() {
      checkModelConstraints();
      return constraints.getIndexHasKeyConstraints();
    }

    @Override
    public List<? extends IExpectConstraint> getExpectConstraints() {
      checkModelConstraints();
      return constraints.getExpectConstraints();
    }

    @Override
    public void addConstraint(@NonNull IAllowedValuesConstraint constraint) {
      checkModelConstraints();
      constraints.addConstraint(constraint);
    }

    @Override
    public void addConstraint(@NonNull IMatchesConstraint constraint) {
      checkModelConstraints();
      constraints.addConstraint(constraint);
    }

    @Override
    public void addConstraint(@NonNull IIndexHasKeyConstraint constraint) {
      checkModelConstraints();
      constraints.addConstraint(constraint);
    }

    @Override
    public void addConstraint(@NonNull IExpectConstraint constraint) {
      checkModelConstraints();
      constraints.addConstraint(constraint);
    }

    @Override
    public MarkupMultiline getRemarks() {
      return XmlInlineFlagDefinition.this.getRemarks();
    }

    @Override
    public IMetaschema getContainingMetaschema() {
      return XmlInlineFlagDefinition.super.getContainingDefinition().getContainingMetaschema();
    }
  }
}
