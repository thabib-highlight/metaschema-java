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

package gov.nist.secauto.metaschema.model; // NOPMD - excessive public methods and coupling is unavoidable

import gov.nist.secauto.metaschema.model.common.AbstractAssemblyInstance;
import gov.nist.secauto.metaschema.model.common.IAssemblyDefinition;
import gov.nist.secauto.metaschema.model.common.IAssemblyInstance;
import gov.nist.secauto.metaschema.model.common.IChoiceInstance;
import gov.nist.secauto.metaschema.model.common.IFieldInstance;
import gov.nist.secauto.metaschema.model.common.IFlagInstance;
import gov.nist.secauto.metaschema.model.common.IInlineDefinition;
import gov.nist.secauto.metaschema.model.common.IMetaschema;
import gov.nist.secauto.metaschema.model.common.IModelContainer;
import gov.nist.secauto.metaschema.model.common.IModelInstance;
import gov.nist.secauto.metaschema.model.common.INamedModelInstance;
import gov.nist.secauto.metaschema.model.common.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.model.common.ModuleScopeEnum;
import gov.nist.secauto.metaschema.model.common.XmlGroupAsBehavior;
import gov.nist.secauto.metaschema.model.common.constraint.IAllowedValuesConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IAssemblyConstraintSupport;
import gov.nist.secauto.metaschema.model.common.constraint.ICardinalityConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IConstraint.ExternalModelSource;
import gov.nist.secauto.metaschema.model.common.constraint.IExpectConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IIndexConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IIndexHasKeyConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IMatchesConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IUniqueConstraint;
import gov.nist.secauto.metaschema.model.common.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.model.common.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.model.common.util.CollectionUtil;
import gov.nist.secauto.metaschema.model.common.util.ObjectUtils;
import gov.nist.secauto.metaschema.model.xmlbeans.InlineAssemblyDefinitionType;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents a Metaschema assembly definition declared locally as an instance.
 */
class XmlInlineAssemblyDefinition
    extends AbstractAssemblyInstance {
  @NonNull
  private final InlineAssemblyDefinitionType xmlAssembly;
  @NonNull
  private final InternalAssemblyDefinition assemblyDefinition;

  /**
   * Constructs a new Metaschema assembly definition from an XML representation bound to Java objects.
   *
   * @param xmlAssembly
   *          the XML representation bound to Java objects
   * @param parent
   *          the parent container, either a choice or assembly
   */
  public XmlInlineAssemblyDefinition(
      @NonNull InlineAssemblyDefinitionType xmlAssembly,
      @NonNull IModelContainer parent) {
    super(parent);
    this.xmlAssembly = xmlAssembly;
    this.assemblyDefinition = new InternalAssemblyDefinition();
  }

  /**
   * Get the underlying XML model.
   *
   * @return the XML model
   */
  protected InlineAssemblyDefinitionType getXmlAssembly() {
    return xmlAssembly;
  }

  @Override
  public InternalAssemblyDefinition getDefinition() {
    return assemblyDefinition;
  }

  @Override
  public String getFormalName() {
    return getXmlAssembly().isSetFormalName() ? getXmlAssembly().getFormalName() : null;
  }

  @SuppressWarnings("null")
  @Override
  public MarkupLine getDescription() {
    return getXmlAssembly().isSetDescription()
        ? MarkupStringConverter.toMarkupString(getXmlAssembly().getDescription())
        : null;
  }

  @Override
  public Map<QName, Set<String>> getProperties() {
    return ModelFactory.toProperties(CollectionUtil.listOrEmpty(getXmlAssembly().getPropList()));
  }

  @SuppressWarnings("null")
  @Override
  public String getName() {
    return getXmlAssembly().getName();
  }

  @Override
  public String getUseName() {
    // an inline definition doesn't have a use name
    return null;
  }

  @Override
  public String getGroupAsName() {
    return getXmlAssembly().isSetGroupAs() ? getXmlAssembly().getGroupAs().getName() : null;
  }

  @Override
  public int getMinOccurs() {
    return XmlModelParser.getMinOccurs(getXmlAssembly().getMinOccurs());
  }

  @Override
  public int getMaxOccurs() {
    return XmlModelParser.getMaxOccurs(getXmlAssembly().getMaxOccurs());
  }

  @Override
  public JsonGroupAsBehavior getJsonGroupAsBehavior() {
    return XmlModelParser.getJsonGroupAsBehavior(getXmlAssembly().getGroupAs());
  }

  @Override
  public XmlGroupAsBehavior getXmlGroupAsBehavior() {
    return XmlModelParser.getXmlGroupAsBehavior(getXmlAssembly().getGroupAs());
  }

  @SuppressWarnings("null")
  @Override
  public MarkupMultiline getRemarks() {
    return getXmlAssembly().isSetRemarks() ? MarkupStringConverter.toMarkupString(getXmlAssembly().getRemarks()) : null;
  }

  @Override
  public Object getValue(@NonNull Object parentValue) {
    // there is no value
    return null;
  }

  @SuppressWarnings("null")
  @Override
  public Collection<?> getItemValues(Object instanceValue) {
    // there are no item values
    return Collections.emptyList();
  }

  /**
   * The corresponding definition for the local flag instance.
   */
  @SuppressWarnings("PMD.GodClass")
  private final class InternalAssemblyDefinition
      implements IAssemblyDefinition, IInlineDefinition<XmlInlineAssemblyDefinition> {
    private XmlFlagContainerSupport flagContainer;
    private XmlModelContainerSupport modelContainer;
    private IAssemblyConstraintSupport constraints;

    @Override
    public boolean isInline() {
      return true;
    }

    @Override
    public XmlInlineAssemblyDefinition getInlineInstance() {
      return XmlInlineAssemblyDefinition.this;
    }

    @Override
    public String getFormalName() {
      return XmlInlineAssemblyDefinition.this.getFormalName();
    }

    @Override
    public MarkupLine getDescription() {
      return XmlInlineAssemblyDefinition.this.getDescription();
    }

    @Override
    public @NonNull Map<QName, Set<String>> getProperties() {
      return XmlInlineAssemblyDefinition.this.getProperties();
    }

    @Override
    public ModuleScopeEnum getModuleScope() {
      return ModuleScopeEnum.LOCAL;
    }

    @Override
    public String getName() {
      return XmlInlineAssemblyDefinition.this.getName();
    }

    @Override
    public String getUseName() {
      // always use the name instead
      return null;
    }

    @Override
    public boolean isRoot() {
      // a local assembly is never a root
      return false;
    }

    @Override
    public String getRootName() {
      return null;
    }

    @Override
    public boolean hasJsonKey() {
      return getXmlAssembly().isSetJsonKey();
    }

    @Override
    public IFlagInstance getJsonKeyFlagInstance() {
      IFlagInstance retval = null;
      if (hasJsonKey()) {
        retval = getFlagInstanceByName(getXmlAssembly().getJsonKey().getFlagRef());
      }
      return retval;
    }

    /**
     * Lazy initialize the flags associated with this definition.
     *
     * @return the flag container instance
     */
    @SuppressWarnings("null")
    private XmlFlagContainerSupport initFlagContainer() {
      synchronized (this) {
        if (flagContainer == null) {
          flagContainer = new XmlFlagContainerSupport(getXmlAssembly(), this);
        }
        return flagContainer;
      }
    }

    @NonNull
    private Map<String, ? extends IFlagInstance> getFlagInstanceMap() {
      return initFlagContainer().getFlagInstanceMap();
    }

    @Override
    public IFlagInstance getFlagInstanceByName(String name) {
      return getFlagInstanceMap().get(name);
    }

    @SuppressWarnings("null")
    @Override
    public Collection<? extends IFlagInstance> getFlagInstances() {
      return getFlagInstanceMap().values();
    }

    /**
     * Lazy initialize the model associated with this definition.
     *
     * @return the flag container instance
     */
    @SuppressWarnings("null")
    private XmlModelContainerSupport initModelContainer() {
      synchronized (this) {
        if (modelContainer == null) {
          modelContainer = new XmlModelContainerSupport(getXmlAssembly(), this);
        }
        return modelContainer;
      }
    }

    private Map<String, ? extends INamedModelInstance> getNamedModelInstanceMap() {
      return initModelContainer().getNamedModelInstanceMap();
    }

    @Override
    public @Nullable INamedModelInstance getModelInstanceByName(String name) {
      return getNamedModelInstanceMap().get(name);
    }

    @SuppressWarnings("null")
    @Override
    public @NonNull Collection<? extends INamedModelInstance> getNamedModelInstances() {
      return getNamedModelInstanceMap().values();
    }

    private Map<String, ? extends IFieldInstance> getFieldInstanceMap() {
      return initModelContainer().getFieldInstanceMap();
    }

    @Override
    public IFieldInstance getFieldInstanceByName(String name) {
      return getFieldInstanceMap().get(name);
    }

    @SuppressWarnings("null")
    @Override
    public Collection<? extends IFieldInstance> getFieldInstances() {
      return getFieldInstanceMap().values();
    }

    private Map<String, ? extends IAssemblyInstance> getAssemblyInstanceMap() {
      return initModelContainer().getAssemblyInstanceMap();
    }

    @Override
    public IAssemblyInstance getAssemblyInstanceByName(String name) {
      return getAssemblyInstanceMap().get(name);
    }

    @SuppressWarnings("null")
    @Override
    public Collection<? extends IAssemblyInstance> getAssemblyInstances() {
      return getAssemblyInstanceMap().values();
    }

    @Override
    public List<? extends IChoiceInstance> getChoiceInstances() {
      return initModelContainer().getChoiceInstances();
    }

    @Override
    public List<? extends IModelInstance> getModelInstances() {
      return initModelContainer().getModelInstances();
    }

    /**
     * Used to generate the instances for the constraints in a lazy fashion when the constraints are
     * first accessed.
     *
     * @return the constraints instance
     */
    private IAssemblyConstraintSupport initModelConstraints() {
      synchronized (this) {
        if (constraints == null) {
          if (getXmlAssembly().isSetConstraint()) {
            constraints = new AssemblyConstraintSupport(
                ObjectUtils.notNull(getXmlAssembly().getConstraint()),
                ExternalModelSource.instance(
                    ObjectUtils.requireNonNull(getContainingMetaschema().getLocation())));
          } else {
            constraints = new AssemblyConstraintSupport();
          }
        }
        return constraints;
      }
    }

    @Override
    public List<? extends IConstraint> getConstraints() {
      return initModelConstraints().getConstraints();
    }

    @Override
    public List<? extends IAllowedValuesConstraint> getAllowedValuesConstraints() {
      return initModelConstraints().getAllowedValuesConstraints();
    }

    @Override
    public List<? extends IMatchesConstraint> getMatchesConstraints() {
      return initModelConstraints().getMatchesConstraints();
    }

    @Override
    public List<? extends IIndexHasKeyConstraint> getIndexHasKeyConstraints() {
      return initModelConstraints().getIndexHasKeyConstraints();
    }

    @Override
    public List<? extends IExpectConstraint> getExpectConstraints() {
      return initModelConstraints().getExpectConstraints();
    }

    @Override
    public List<? extends IIndexConstraint> getIndexConstraints() {
      return initModelConstraints().getIndexConstraints();
    }

    @Override
    public List<? extends IUniqueConstraint> getUniqueConstraints() {
      return initModelConstraints().getUniqueConstraints();
    }

    @Override
    public List<? extends ICardinalityConstraint> getHasCardinalityConstraints() {
      return initModelConstraints().getHasCardinalityConstraints();
    }

    @Override
    public void addConstraint(@NonNull IAllowedValuesConstraint constraint) {
      initModelConstraints().addConstraint(constraint);
    }

    @Override
    public void addConstraint(@NonNull IMatchesConstraint constraint) {
      initModelConstraints().addConstraint(constraint);
    }

    @Override
    public void addConstraint(@NonNull IIndexHasKeyConstraint constraint) {
      initModelConstraints().addConstraint(constraint);
    }

    @Override
    public void addConstraint(@NonNull IExpectConstraint constraint) {
      initModelConstraints().addConstraint(constraint);
    }

    @Override
    public void addConstraint(@NonNull IIndexConstraint constraint) {
      initModelConstraints().addConstraint(constraint);
    }

    @Override
    public void addConstraint(@NonNull IUniqueConstraint constraint) {
      initModelConstraints().addConstraint(constraint);
    }

    @Override
    public void addConstraint(@NonNull ICardinalityConstraint constraint) {
      initModelConstraints().addConstraint(constraint);
    }

    @Override
    public MarkupMultiline getRemarks() {
      return XmlInlineAssemblyDefinition.this.getRemarks();
    }

    @Override
    public IMetaschema getContainingMetaschema() {
      return XmlInlineAssemblyDefinition.super.getContainingDefinition().getContainingMetaschema();
    }
  }
}
