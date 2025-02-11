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

package gov.nist.secauto.metaschema.codegen;

import org.glassfish.jaxb.core.api.impl.NameConverter;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A variety of utility methods for normalizing Java class related names.
 */
public final class ClassUtils {
  private ClassUtils() {
    // disable construction
  }

  /**
   * Transforms the provided name into a string suitable for use as a Java property name.
   *
   * @param name
   *          the name of an information element definition
   * @return a Java property name
   */
  @SuppressWarnings("null")
  @NonNull
  public static String toPropertyName(@NonNull String name) {
    return NameConverter.standard.toPropertyName(name);
  }

  /**
   * Transforms the provided name into a string suitable for use as a Java variable name.
   *
   * @param name
   *          the name of an information element definition
   * @return a Java variable name
   */
  @SuppressWarnings("null")
  @NonNull
  public static String toVariableName(@NonNull String name) {
    return NameConverter.standard.toVariableName(name);
  }

  /**
   * Transforms the provided name into a string suitable for use as a Java class name.
   *
   * @param name
   *          the name of an information element definition
   * @return a Java variable name
   */
  @SuppressWarnings("null")
  @NonNull
  public static String toClassName(@NonNull String name) {
    return NameConverter.standard.toClassName(name);
  }

  /**
   * Transforms the provided name into a string suitable for use as a Java package name.
   *
   * @param name
   *          the name of an information element definition
   * @return a Java variable name
   */
  @SuppressWarnings("null")
  @NonNull
  public static String toPackageName(@NonNull String name) {
    return NameConverter.standard.toPackageName(name);
  }

}
