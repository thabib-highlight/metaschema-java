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

package gov.nist.secauto.metaschema.binding.io;

import gov.nist.secauto.metaschema.binding.IBindingContext;
import gov.nist.secauto.metaschema.binding.model.IAssemblyClassBinding;
import gov.nist.secauto.metaschema.model.common.configuration.DefaultConfiguration;
import gov.nist.secauto.metaschema.model.common.configuration.IConfigurationFeature;
import gov.nist.secauto.metaschema.model.common.configuration.IMutableConfiguration;
import gov.nist.secauto.metaschema.model.common.util.ObjectUtils;

import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

@SuppressWarnings("PMD.ReplaceVectorWithList") // false positive
abstract class AbstractSerializationBase<T extends IConfigurationFeature<?>>
    implements IMutableConfiguration<T> {
  @NonNull
  private final IBindingContext bindingContext;
  @NonNull
  private final IAssemblyClassBinding classBinding;
  @NonNull
  private final DefaultConfiguration<T> configuration;

  protected AbstractSerializationBase(
      @NonNull IBindingContext bindingContext,
      @NonNull IAssemblyClassBinding classBinding) {
    this.bindingContext = ObjectUtils.requireNonNull(bindingContext, "bindingContext");
    this.classBinding = ObjectUtils.requireNonNull(classBinding, "classBinding");
    this.configuration = new DefaultConfiguration<>();
  }

  /**
   * Retrieve the binding context associated with the serializer.
   *
   * @return the binding context
   */
  @NonNull
  protected IBindingContext getBindingContext() {
    return bindingContext;
  }

  /**
   * Retrieve the bound class information associated with the assembly that the
   * serializer/deserializer will write/read data from.
   *
   * @return the class binding for the Metaschema assembly
   */
  @NonNull
  protected IAssemblyClassBinding getClassBinding() {
    return classBinding;
  }

  @SuppressWarnings("unused")
  protected void configurationChanged(@NonNull IMutableConfiguration<T> config) {
    // do nothing by default. Methods can override this to deal with factory caching
  }

  /**
   * Get the current configuration of the serializer/deserializer.
   *
   * @return the configuration
   */
  @NonNull
  protected IMutableConfiguration<T> getConfiguration() {
    return configuration;
  }

  @Override
  public boolean isFeatureEnabled(T feature) {
    return configuration.isFeatureEnabled(feature);
  }

  @Override
  public Map<T, Object> getFeatureValues() {
    return configuration.getFeatureValues();
  }

  @Override
  public <V> V get(T feature) {
    return configuration.get(feature);
  }

}
