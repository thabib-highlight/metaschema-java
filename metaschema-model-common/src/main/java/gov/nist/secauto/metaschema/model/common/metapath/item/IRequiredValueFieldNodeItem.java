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

package gov.nist.secauto.metaschema.model.common.metapath.item;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IRequiredValueFieldNodeItem extends IFieldNodeItem, IRequiredValueModelNodeItem {

  @Override
  default IRequiredValueFieldNodeItem getNodeItem() {
    return this;
  }

  @Override
  IRequiredValueAssemblyNodeItem getParentNodeItem();

  @Override
  default IRequiredValueAssemblyNodeItem getParentContentNodeItem() {
    return getParentNodeItem();
  }

  @Override
  @NonNull
  IAnyAtomicItem toAtomicItem();

  /**
   * Fields do not have model items. This call should return an empty collection.
   */
  @SuppressWarnings("null")
  @Override
  default Collection<? extends List<? extends IRequiredValueModelNodeItem>> getModelItems() {
    // a field does not have model items
    return Collections.emptyList();
  }

  /**
   * Fields do not have model items. This call should return an empty list.
   */
  @SuppressWarnings("null")
  @Override
  default List<? extends IRequiredValueModelNodeItem> getModelItemsByName(String name) {
    // a field does not have model items
    return Collections.emptyList();
  }

  /**
   * Fields do not have model items. This call should return an empty stream.
   */
  @SuppressWarnings("null")
  @NonNull
  @Override
  default Stream<? extends IRequiredValueModelNodeItem> modelItems() {
    return Stream.empty();
  }
}
