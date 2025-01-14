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

import gov.nist.secauto.metaschema.model.common.metapath.format.IPathFormatter;
import gov.nist.secauto.metaschema.model.common.util.ObjectUtils;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IDocumentNodeItem extends IRequiredValueNodeItem {
  @Override
  default NodeItemType getNodeItemType() {
    return NodeItemType.DOCUMENT;
  }

  @Override
  default IDocumentNodeItem getNodeItem() {
    return this;
  }

  /**
   * Get the root assembly associated with this document.
   *
   * @return the root assembly
   */
  @NonNull
  IRootAssemblyNodeItem getRootAssemblyNodeItem();

  @Override
  default IRequiredValueModelNodeItem getParentContentNodeItem() {
    // there is no parent
    return null;
  }

  @Override
  default IRequiredValueNodeItem getParentNodeItem() {
    // there is no parent
    return null;
  }

  /**
   * Get the URI associated with this document.
   *
   * @return the document's URI
   */
  @NonNull
  URI getDocumentUri();

  @Override
  @NonNull
  default URI getBaseUri() {
    return getDocumentUri();
  }

  /**
   * Documents do not have flag items. This call should return an empty collection.
   */
  @SuppressWarnings("null")
  @Override
  default Collection<? extends IRequiredValueFlagNodeItem> getFlags() {
    // a document does not have flags
    return Collections.emptyList();
  }

  /**
   * Documents do not have flag items. This call should return {@code null}.
   */
  @Override
  default IRequiredValueFlagNodeItem getFlagByName(@NonNull String name) {
    // a document does not have flags
    return null;
  }

  /**
   * Documents do not have flag items. This call should return an empty stream.
   */
  @SuppressWarnings("null")
  @Override
  default @NonNull Stream<? extends IRequiredValueFlagNodeItem> flags() {
    // a document does not have flags
    return Stream.empty();
  }

  @SuppressWarnings("null")
  @Override
  default Stream<? extends IModelNodeItem> modelItems() {
    return Stream.of(getRootAssemblyNodeItem());
  }

  @SuppressWarnings("null")
  @Override
  default @NonNull List<? extends IRequiredValueModelNodeItem> getModelItemsByName(String name) {
    IRootAssemblyNodeItem root = getRootAssemblyNodeItem();
    return root.getName().equals(name) ? Collections.singletonList(root) : Collections.emptyList();
  }

  @SuppressWarnings("null")
  @Override
  default @NonNull Collection<? extends List<? extends IRequiredValueModelNodeItem>> getModelItems() {
    return Collections.singletonList(Collections.singletonList(getRootAssemblyNodeItem()));
  }

  @Override
  default @NonNull String format(@NonNull IPathFormatter formatter) {
    return formatter.formatDocument(this);
  }

  @Override
  default <RESULT, CONTEXT> RESULT accept(@NonNull INodeItemVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitDocument(this, context);
  }

  @Override
  @NonNull
  default Object getValue() {
    return ObjectUtils.requireNonNull(getRootAssemblyNodeItem().getValue());
  }
}
