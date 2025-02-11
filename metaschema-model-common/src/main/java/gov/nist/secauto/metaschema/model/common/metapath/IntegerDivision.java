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

import gov.nist.secauto.metaschema.model.common.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.model.common.metapath.function.OperationFunctions;
import gov.nist.secauto.metaschema.model.common.metapath.item.IIntegerItem;
import gov.nist.secauto.metaschema.model.common.metapath.item.INumericItem;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

class IntegerDivision
    extends AbstractArithmeticExpression<IIntegerItem> {

  /**
   * Create an expression that gets the whole number quotient result by dividing the dividend by the
   * divisor.
   *
   * @param dividend
   *          the expression whose item result will be divided
   * @param divisor
   *          the expression whose item result will be divided by
   */
  protected IntegerDivision(@NonNull IExpression dividend, @NonNull IExpression divisor) {
    super(dividend, divisor, IIntegerItem.class);
  }

  @Override
  public Class<IIntegerItem> getBaseResultType() {
    return IIntegerItem.class;
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitIntegerDivision(this, context);
  }

  @Override
  public ISequence<? extends IIntegerItem> accept(DynamicContext dynamicContext, INodeContext context) {
    INumericItem dividend = FunctionUtils.toNumericOrNull(
        getFirstDataItem(getLeft().accept(dynamicContext, context), true));
    INumericItem divisor = FunctionUtils.toNumericOrNull(
        getFirstDataItem(getRight().accept(dynamicContext, context), true));

    return resultOrEmpty(dividend, divisor);
  }

  /**
   * Get the whole number quotient result by dividing the dividend by the divisor.
   *
   * @param dividend
   *          the item to be divided
   * @param divisor
   *          the item to divide by
   * @return the quotient result or an empty {@link ISequence} if either item is {@code null}
   */
  @NonNull
  protected static ISequence<? extends IIntegerItem> resultOrEmpty(@Nullable INumericItem dividend,
      @Nullable INumericItem divisor) {
    ISequence<? extends IIntegerItem> retval;
    if (dividend == null || divisor == null) {
      retval = ISequence.empty();
    } else {
      IIntegerItem result = divide(dividend, divisor);
      retval = ISequence.of(result);
    }
    return retval;
  }

  /**
   * Get the whole number quotient result by dividing the dividend by the divisor.
   *
   * @param dividend
   *          the item to be divided
   * @param divisor
   *          the item to divide by
   * @return the quotient result
   */
  public static IIntegerItem divide(@NonNull INumericItem dividend, @NonNull INumericItem divisor) {
    return OperationFunctions.opNumericIntegerDivide(dividend, divisor);
  }
}
