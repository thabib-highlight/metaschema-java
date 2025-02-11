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

import gov.nist.secauto.metaschema.model.common.IFieldDefinition;
import gov.nist.secauto.metaschema.model.common.IFieldInstance;

import java.net.URI;
import java.util.Map;
import java.util.function.Supplier;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * a new {@link INodeItem} instance, that is orphaned from any parent nodes, supported by an
 * {@link IFieldDefinition}.
 */
class FieldDefinitionNodeItemImpl
    extends AbstractNodeContext<
        IFlagNodeItem,
        AbstractNodeContext.Flags<IFlagNodeItem>>
    implements IFieldNodeItem {
  @NonNull
  private final IFieldDefinition definition;
  @Nullable
  private final URI baseUri;

  /**
   * Construct a new {@link INodeItem} instance, that is orphaned from any parent nodes, based on the
   * provided field {@code definition}.
   *
   * @param definition
   *          the field
   * @param baseUri
   *          an optional base URI to use for resolving relative URIs
   * @param factory
   *          the factory to use to instantiate new node items
   */
  public FieldDefinitionNodeItemImpl(
      @NonNull IFieldDefinition definition,
      @Nullable URI baseUri,
      @NonNull INodeItemFactory factory) {
    super(factory);
    this.definition = definition;
    this.baseUri = baseUri;
  }

  @Override
  protected @NonNull Supplier<Flags<IFlagNodeItem>>
      newModelSupplier(@NonNull INodeItemFactory factory) {
    return () -> {
      Map<String, IFlagNodeItem> flags = factory.generateFlags(this);
      return new Flags<>(flags);
    };
  }

  @Override
  public IAssemblyNodeItem getParentNodeItem() {
    // this is an orphaned definition
    return null;
  }

  @Override
  public IFieldDefinition getDefinition() {
    return definition;
  }

  @Override
  public IFieldInstance getInstance() {
    // there is no instance
    return null;
  }

  @Override
  public URI getBaseUri() {
    return baseUri;
  }

  @Override
  public int getPosition() {
    return 1;
  }

  @Override
  public IAnyAtomicItem toAtomicItem() {
    // does not have a value
    return null;
  }

  @Override
  public Object getValue() {
    // there is no value
    return null;
  }
}
