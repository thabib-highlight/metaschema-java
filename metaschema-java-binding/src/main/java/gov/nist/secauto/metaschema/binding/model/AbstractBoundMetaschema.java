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

import gov.nist.secauto.metaschema.binding.IBindingContext;
import gov.nist.secauto.metaschema.binding.model.annotations.Metaschema;
import gov.nist.secauto.metaschema.model.common.AbstractMetaschema;
import gov.nist.secauto.metaschema.model.common.IAssemblyDefinition;
import gov.nist.secauto.metaschema.model.common.IFieldDefinition;
import gov.nist.secauto.metaschema.model.common.IFlagDefinition;
import gov.nist.secauto.metaschema.model.common.IMetaschema;
import gov.nist.secauto.metaschema.model.common.util.CollectionUtil;
import gov.nist.secauto.metaschema.model.common.util.ObjectUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractBoundMetaschema
    extends AbstractMetaschema {
  @NonNull
  private final IBindingContext bindingContext;
  private Map<String, IAssemblyClassBinding> assemblyDefinitions;
  private Map<String, IFieldClassBinding> fieldDefinitions;

  /**
   * Create a new Metaschema instance for a given class annotated by the {@link Metaschema}
   * annotation.
   * <p>
   * Will also load any imported Metaschemas.
   *
   *
   * @param clazz
   *          the Metaschema class
   * @param bindingContext
   *          the Metaschema binding context
   * @return the new Metaschema instance
   */
  @NonNull
  public static IMetaschema createInstance(
      @NonNull Class<? extends IMetaschema> clazz,
      @NonNull IBindingContext bindingContext) {

    if (!clazz.isAnnotationPresent(Metaschema.class)) {
      throw new IllegalStateException(String.format("The class '%s' is missing the '%s' annotation",
          clazz.getCanonicalName(), Metaschema.class.getCanonicalName()));
    }

    Metaschema metaschemaAnnotation = clazz.getAnnotation(Metaschema.class);

    List<IMetaschema> importedMetaschemas;
    if (metaschemaAnnotation.imports().length > 0) {
      importedMetaschemas = new ArrayList<>(metaschemaAnnotation.imports().length);
      for (Class<? extends IMetaschema> importClass : metaschemaAnnotation.imports()) {
        assert importClass != null;
        IMetaschema metaschemaImport = bindingContext.getMetaschemaInstanceByClass(importClass);
        importedMetaschemas.add(metaschemaImport);
      }
    } else {
      importedMetaschemas = CollectionUtil.emptyList();
    }
    return createInstance(clazz, bindingContext, importedMetaschemas);
  }

  @NonNull
  private static IMetaschema createInstance(
      @NonNull Class<? extends IMetaschema> clazz,
      @NonNull IBindingContext bindingContext,
      @NonNull List<? extends IMetaschema> importedMetaschemas) {

    Constructor<? extends IMetaschema> constructor;
    try {
      constructor = clazz.getDeclaredConstructor(List.class, IBindingContext.class);
    } catch (NoSuchMethodException ex) {
      throw new IllegalArgumentException(ex);
    }

    try {
      return ObjectUtils.notNull(constructor.newInstance(importedMetaschemas, bindingContext));
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
      throw new IllegalArgumentException(ex);
    }
  }

  /**
   * Construct a new Metaschema instance.
   *
   * @param importedMetaschema
   *          Metaschema imports associated with the metaschema
   * @param bindingContext
   *          the Metaschema binding context
   */
  protected AbstractBoundMetaschema(
      @NonNull List<? extends IMetaschema> importedMetaschema,
      @NonNull IBindingContext bindingContext) {
    super(importedMetaschema);
    this.bindingContext = bindingContext;
  }

  /**
   * Get the Metaschema binding context.
   *
   * @return the context
   */
  @NonNull
  protected IBindingContext getBindingContext() {
    return bindingContext;
  }

  @Override
  public URI getLocation() { // NOPMD - intentional
    // not known
    return null;
  }

  @NonNull
  protected Class<?>[] getAssemblyClasses() {
    Class<?>[] retval;
    if (getClass().isAnnotationPresent(Metaschema.class)) {
      Metaschema metaschemaAnnotation = getClass().getAnnotation(Metaschema.class);
      retval = metaschemaAnnotation.assemblies();
    } else {
      retval = new Class<?>[] {};
    }
    return retval;
  }

  @NonNull
  protected Class<?>[] getFieldClasses() {
    Class<?>[] retval;
    if (getClass().isAnnotationPresent(Metaschema.class)) {
      Metaschema metaschemaAnnotation = getClass().getAnnotation(Metaschema.class);
      retval = metaschemaAnnotation.fields();
    } else {
      retval = new Class<?>[] {};
    }
    return retval;
  }

  protected void initDefinitions() {
    synchronized (this) {
      if (assemblyDefinitions == null) {
        IBindingContext bindingContext = getBindingContext();
        this.assemblyDefinitions = Arrays.stream(getAssemblyClasses())
            .map(clazz -> {
              assert clazz != null;
              return (IAssemblyClassBinding) ObjectUtils.requireNonNull(bindingContext.getClassBinding(clazz));
            })
            .collect(Collectors.toUnmodifiableMap(
                IAssemblyClassBinding::getName,
                Function.identity()));
        this.fieldDefinitions = Arrays.stream(getFieldClasses())
            .map(clazz -> {
              assert clazz != null;
              return (IFieldClassBinding) ObjectUtils.requireNonNull(bindingContext.getClassBinding(clazz));
            })
            .collect(Collectors.toUnmodifiableMap(
                IFieldClassBinding::getName,
                Function.identity()));
      }
    }

  }

  @SuppressWarnings("null")
  protected @NonNull Map<String, ? extends IAssemblyDefinition> getAssemblyDefinitionMap() {
    initDefinitions();
    return assemblyDefinitions;
  }

  @SuppressWarnings("null")
  @Override
  public Collection<? extends IAssemblyDefinition> getAssemblyDefinitions() {
    return getAssemblyDefinitionMap().values();
  }

  @Override
  public IAssemblyDefinition getAssemblyDefinitionByName(@NonNull String name) {
    return getAssemblyDefinitionMap().get(name);
  }

  protected Map<String, ? extends IFieldDefinition> getFieldDefinitionMap() {
    initDefinitions();
    return fieldDefinitions;
  }

  @SuppressWarnings("null")
  @Override
  public Collection<? extends IFieldDefinition> getFieldDefinitions() {
    return getFieldDefinitionMap().values();
  }

  @Override
  public IFieldDefinition getFieldDefinitionByName(@NonNull String name) {
    return getFieldDefinitionMap().get(name);
  }

  @NonNull
  public Map<String, ? extends IFlagDefinition> getFlagDefinitionMap() {
    // Flags are always inline
    return CollectionUtil.emptyMap();
  }

  @SuppressWarnings("null")
  @Override
  public Collection<? extends IFlagDefinition> getFlagDefinitions() {
    // Flags are always inline
    return Collections.emptyList();
  }

  @Override
  public IFlagDefinition getFlagDefinitionByName(@NonNull String name) { // NOPMD - intentional
    // Flags are always inline
    return null;
  }
}
