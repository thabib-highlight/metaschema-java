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

package gov.nist.secauto.metaschema.binding.model;

import gov.nist.secauto.metaschema.binding.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.binding.model.annotations.Constants;
import gov.nist.secauto.metaschema.binding.model.annotations.JsonFieldValueKeyFlag;
import gov.nist.secauto.metaschema.binding.model.annotations.JsonKey;
import gov.nist.secauto.metaschema.binding.model.annotations.NullJavaTypeAdapter;
import gov.nist.secauto.metaschema.binding.model.annotations.ValueConstraints;
import gov.nist.secauto.metaschema.model.common.IFlagDefinition;
import gov.nist.secauto.metaschema.model.common.IMetaschema;
import gov.nist.secauto.metaschema.model.common.ModuleScopeEnum;
import gov.nist.secauto.metaschema.model.common.constraint.IConstraint.InternalModelSource;
import gov.nist.secauto.metaschema.model.common.constraint.IValueConstraintSupport;
import gov.nist.secauto.metaschema.model.common.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.model.common.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.model.common.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.model.common.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.model.common.util.ObjectUtils;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import nl.talsmasoftware.lazy4j.Lazy;

class DefaultFlagProperty
    extends AbstractFlagProperty {
  // private static final Logger logger = LogManager.getLogger(DefaultFlagProperty.class);
  @NonNull
  private final Field field;
  @NonNull
  private final BoundFlag flag;
  @NonNull
  private final IDataTypeAdapter<?> javaTypeAdapter;
  @Nullable
  private final Object defaultValue;
  private InternalFlagDefinition definition;

  /**
   * Construct a new bound flag instance based on a Java property. The name of the property is bound
   * to the name of the instance.
   *
   * @param field
   *          the Java field to bind to
   * @param parentClassBinding
   *          the class binding for the field's containing class
   */
  public DefaultFlagProperty(
      @NonNull Field field,
      @NonNull IClassBinding parentClassBinding) {
    super(parentClassBinding);
    this.field = ObjectUtils.requireNonNull(field, "field");
    this.flag = ObjectUtils.requireNonNull(field.getAnnotation(BoundFlag.class));

    Class<? extends IDataTypeAdapter<?>> adapterClass = ObjectUtils.notNull(this.flag.typeAdapter());
    if (NullJavaTypeAdapter.class.equals(adapterClass)) {
      this.javaTypeAdapter = MetaschemaDataTypeProvider.DEFAULT_DATA_TYPE;
    } else {
      this.javaTypeAdapter = ObjectUtils.requireNonNull(
          parentClassBinding.getBindingContext().getJavaTypeAdapterInstance(adapterClass));
    }

    String defaultString = this.flag.defaultValue();
    this.defaultValue = Constants.NULL_VALUE.equals(defaultString) ? null // NOPMD readability
        : getJavaTypeAdapter().parse(defaultString);
  }

  @Override
  @NonNull
  public Field getField() {
    return field;
  }

  @NonNull
  protected BoundFlag getFlagAnnotation() {
    return flag;
  }

  public Object getDefaultValue() {
    return this.defaultValue;
  }

  @Override
  public boolean isJsonKey() {
    return getField().isAnnotationPresent(JsonKey.class);
  }

  @Override
  public boolean isJsonValueKey() {
    return getField().isAnnotationPresent(JsonFieldValueKeyFlag.class);
  }

  @Override
  public boolean isRequired() {
    return getFlagAnnotation().required();
  }

  @NonNull
  protected final IDataTypeAdapter<?> getJavaTypeAdapter() {
    return javaTypeAdapter;
  }

  @Override
  public String getFormalName() {
    return ModelUtil.resolveToString(getFlagAnnotation().formalName());
  }

  @Override
  public MarkupLine getDescription() {
    return ModelUtil.resolveToMarkupLine(getFlagAnnotation().description());
  }

  @Override
  public String getUseName() {
    return ModelUtil.resolveToString(getFlagAnnotation().useName());
  }

  @Override
  public String getXmlNamespace() {
    return ModelUtil.resolveOptionalNamespace(getFlagAnnotation().namespace(), getParentClassBinding());
  }

  @Override
  public MarkupMultiline getRemarks() {
    return ModelUtil.resolveToMarkupMultiline(getFlagAnnotation().remarks());
  }

  @SuppressWarnings("null")
  @Override
  public IFlagDefinition getDefinition() {
    synchronized (this) {
      if (definition == null) {
        definition = new InternalFlagDefinition();
      }
      return definition;
    }
  }

  @SuppressWarnings("null")
  @Override
  public String toCoordinates() {
    return String.format("%s Instance(%s): %s:%s",
        getModelType().name().toLowerCase(Locale.ROOT),
        getName(),
        getParentClassBinding().getBoundClass().getName(),
        getField().getName());
  }

  private final class InternalFlagDefinition implements IFlagDefinition, IValueConstraintFeature {
    private final Lazy<IValueConstraintSupport> constraints;

    private InternalFlagDefinition() {
      this.constraints = Lazy.lazy(() -> new ValueConstraintSupport(
          getField().getAnnotation(ValueConstraints.class),
          InternalModelSource.instance()));
    }

    @Override
    public IValueConstraintSupport getConstraintSupport() {
      return constraints.get();
    }

    @Override
    public String getFormalName() {
      return DefaultFlagProperty.this.getFormalName();
    }

    @Override
    public MarkupLine getDescription() {
      return DefaultFlagProperty.this.getDescription();
    }

    @Override
    public @NonNull Map<QName, Set<String>> getProperties() {
      // TODO: implement
      throw new UnsupportedOperationException();
    }

    @Override
    public IDataTypeAdapter<?> getJavaTypeAdapter() {
      return DefaultFlagProperty.this.getJavaTypeAdapter();
    }

    @Override
    public Object getDefaultValue() {
      return DefaultFlagProperty.this.getDefaultValue();
    }

    @Override
    public boolean isInline() {
      return true;
    }

    @Override
    public IBoundFlagInstance getInlineInstance() {
      return DefaultFlagProperty.this;
    }

    @Override
    public String getName() {
      return getJavaFieldName();
    }

    @Override
    public String getUseName() {
      // always use the name instead
      return null;
    }

    @Override
    public MarkupMultiline getRemarks() {
      return DefaultFlagProperty.this.getRemarks();
    }

    @Override
    public String toCoordinates() {
      return DefaultFlagProperty.this.toCoordinates();
    }

    @Override
    public @NonNull ModuleScopeEnum getModuleScope() {
      // TODO: is this the right value?
      return ModuleScopeEnum.INHERITED;
    }

    @Override
    public IMetaschema getContainingMetaschema() {
      return DefaultFlagProperty.this.getContainingMetaschema();
    }
  }
}
