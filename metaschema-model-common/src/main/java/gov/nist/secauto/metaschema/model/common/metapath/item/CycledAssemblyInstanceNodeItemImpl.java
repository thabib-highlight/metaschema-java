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

import gov.nist.secauto.metaschema.model.common.IAssemblyDefinition;
import gov.nist.secauto.metaschema.model.common.IAssemblyInstance;

import java.util.Collection;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

class CycledAssemblyInstanceNodeItemImpl implements ICycledAssemblyNodeItem {
  @NonNull
  private final IAssemblyInstance instance;
  @NonNull
  private final IAssemblyNodeItem parent;
  @NonNull
  private final IAssemblyNodeItem cycledNodeItem;

  /**
   * Construct a new assembly node item that represents a loop back to a previously declared item.
   *
   * @param instance
   *          the instance in the parent's model
   * @param parent
   *          the parent containing the instance
   * @param cycledNodeItem
   *          the original node item at the start of the loop
   */
  public CycledAssemblyInstanceNodeItemImpl(
      @NonNull IAssemblyInstance instance,
      @NonNull IAssemblyNodeItem parent,
      @NonNull IAssemblyNodeItem cycledNodeItem) {
    this.instance = instance;
    this.parent = parent;
    this.cycledNodeItem = cycledNodeItem;
  }

  @Override
  public IAssemblyNodeItem getCycledNodeItem() {
    return cycledNodeItem;
  }

  @Override
  public int getPosition() {
    return 1;
  }

  @Override
  public IAssemblyNodeItem getParentNodeItem() {
    return parent;
  }

  @Override
  public @Nullable Object getValue() {
    // there is no value
    return null;
  }

  @Override
  public Collection<? extends IFlagNodeItem> getFlags() {
    return getCycledNodeItem().getFlags();
  }

  @Override
  public IFlagNodeItem getFlagByName(@NonNull String name) {
    return getCycledNodeItem().getFlagByName(name);
  }

  @Override
  public Collection<? extends List<? extends IModelNodeItem>> getModelItems() {
    return getCycledNodeItem().getModelItems();
  }

  @Override
  public List<? extends IModelNodeItem> getModelItemsByName(String name) {
    return getCycledNodeItem().getModelItemsByName(name);
  }

  @Override
  public IAssemblyDefinition getDefinition() {
    return getInstance().getDefinition();
  }

  @Override
  public IAssemblyInstance getInstance() {
    return instance;
  }

}
