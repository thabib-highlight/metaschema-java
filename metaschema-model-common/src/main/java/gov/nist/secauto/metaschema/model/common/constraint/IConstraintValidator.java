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

import gov.nist.secauto.metaschema.model.common.metapath.MetapathException;
import gov.nist.secauto.metaschema.model.common.metapath.item.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.model.common.metapath.item.IDocumentNodeItem;
import gov.nist.secauto.metaschema.model.common.metapath.item.IFieldNodeItem;
import gov.nist.secauto.metaschema.model.common.metapath.item.IFlagNodeItem;
import gov.nist.secauto.metaschema.model.common.metapath.item.INodeItem;

import org.jetbrains.annotations.NotNull;

/**
 * This interface provides an entry point for performing validations over Metapath items associated
 * with a Metaschema model.
 */
public interface IConstraintValidator {
  /**
   * Validate the provided item against any associated constraints.
   * 
   * @param item
   *          the node item to validate
   * @throws MetapathException
   *           if an error occurred while evaluating a Metapath used in a constraint
   */
  void validate(@NotNull INodeItem item);

  /**
   * Validate the provided document item against any associated constraints.
   * 
   * @param item
   *          the document item to validate
   * @throws MetapathException
   *           if an error occurred while evaluating a Metapath used in a constraint
   */
  void validate(@NotNull IDocumentNodeItem item);

  /**
   * Validate the provided flag item against any associated constraints.
   * 
   * @param item
   *          the flag item to validate
   * @throws MetapathException
   *           if an error occurred while evaluating a Metapath used in a constraint
   */
  void validate(@NotNull IFlagNodeItem item);

  /**
   * Validate the provided field item against any associated constraints.
   * 
   * @param item
   *          the field item to validate
   * @throws MetapathException
   *           if an error occurred while evaluating a Metapath used in a constraint
   */
  void validate(@NotNull IFieldNodeItem item);

  /**
   * Validate the provided assembly item against any associated constraints.
   * 
   * @param item
   *          the assembly item to validate
   * @throws MetapathException
   *           if an error occurred while evaluating a Metapath used in a constraint
   */
  void validate(@NotNull IAssemblyNodeItem item);

  /**
   * Complete any validations that require full analysis of the content model.
   * 
   * @throws MetapathException
   *           if an error occurred while evaluating a Metapath used in a constraint
   */
  void finalizeValidation();
}
