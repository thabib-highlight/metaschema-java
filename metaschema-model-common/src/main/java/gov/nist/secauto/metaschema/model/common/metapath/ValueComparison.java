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

package gov.nist.secauto.metaschema.model.common.metapath;

import gov.nist.secauto.metaschema.model.common.metapath.item.IAnyAtomicItem;
import gov.nist.secauto.metaschema.model.common.metapath.item.IBooleanItem;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

class ValueComparison
    extends AbstractComparison {

  /**
   * Create a new value comparison expression.
   *
   * @param left
   *          the expression to compare against
   * @param operator
   *          the comparison operator
   * @param right
   *          the expression to compare with
   */
  protected ValueComparison(@NonNull IExpression left, @NonNull Operator operator, @NonNull IExpression right) {
    super(left, operator, right);
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitValueComparison(this, context);
  }

  @Override
  public ISequence<? extends IBooleanItem> accept(DynamicContext dynamicContext, INodeContext context) {
    IAnyAtomicItem left = getFirstDataItem(getLeft().accept(dynamicContext, context), false);
    IAnyAtomicItem right = getFirstDataItem(getRight().accept(dynamicContext, context), false);

    return resultOrEmpty(left, right);
  }

  /**
   * Compare the two atomic items.
   *
   * @param leftItem
   *          the first item to compare
   * @param rightItem
   *          the second item to compare
   * @return a or an empty {@link ISequence} if either item is {@code null}
   */
  @NonNull
  protected ISequence<? extends IBooleanItem> resultOrEmpty(@Nullable IAnyAtomicItem leftItem,
      @Nullable IAnyAtomicItem rightItem) {
    ISequence<? extends IBooleanItem> retval;
    if (leftItem == null || rightItem == null) {
      retval = ISequence.empty();
    } else {
      IBooleanItem result = valueCompairison(leftItem, getOperator(), rightItem);
      retval = ISequence.of(result);
    }
    return retval;
  }

  /**
   * Compare the two items using the provided {@code operator}.
   *
   * @param leftItem
   *          the first item to compare
   * @param operator
   *          the comparison operator
   * @param rightItem
   *          the second item to compare
   * @return the result of the comparison
   */
  @NonNull
  protected IBooleanItem valueCompairison(@NonNull IAnyAtomicItem leftItem, @NonNull Operator operator,
      @NonNull IAnyAtomicItem rightItem) {
    return compare(leftItem, operator, rightItem);
  }
}
