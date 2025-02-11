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

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.model.common.metapath.item.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.model.common.metapath.item.IDefinitionNodeItem;
import gov.nist.secauto.metaschema.model.common.metapath.item.IFieldNodeItem;
import gov.nist.secauto.metaschema.model.common.metapath.item.IFlagNodeItem;
import gov.nist.secauto.metaschema.model.common.metapath.item.IModelNodeItem;
import gov.nist.secauto.metaschema.model.common.metapath.item.INodeItem;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.auto.Mock;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

class ExpressionUtilsTest {

  @RegisterExtension
  Mockery context = new JUnit5Mockery();

  @Mock
  private IFlagNodeItem flagNodeItem1; // NOPMD - it's injected
  @Mock
  private IFlagNodeItem flagNodeItem2; // NOPMD - it's injected

  @Mock
  private IExpression basicFlagExpr1; // NOPMD - it's injected
  @Mock
  private IExpression basicFlagExpr2; // NOPMD - it's injected
  @Mock
  private IExpression basicAssemblyExpr; // NOPMD - it's injected
  @Mock
  private IExpression basicFieldExpr; // NOPMD - it's injected

  @Test
  void testTwoFlags() {
    Class<INodeItem> baseType = INodeItem.class;

    context.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(basicFlagExpr1).getStaticResultType();
        will(returnValue(IFlagNodeItem.class));
        allowing(basicFlagExpr2).getStaticResultType();
        will(returnValue(IFlagNodeItem.class));
      }
    });
    @SuppressWarnings("null") Class<? extends INodeItem> result
        = ExpressionUtils.analyzeStaticResultType(baseType, List.of(basicFlagExpr1, basicFlagExpr2));
    assertEquals(IFlagNodeItem.class, result);
  }

  @Test
  void testFlagAndAssembly() {
    Class<INodeItem> baseType = INodeItem.class;

    context.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(basicFlagExpr1).getStaticResultType();
        will(returnValue(IFlagNodeItem.class));
        allowing(basicAssemblyExpr).getStaticResultType();
        will(returnValue(IAssemblyNodeItem.class));
      }
    });
    @SuppressWarnings("null") Class<? extends INodeItem> result
        = ExpressionUtils.analyzeStaticResultType(baseType, List.of(basicFlagExpr1, basicAssemblyExpr));
    assertEquals(IDefinitionNodeItem.class, result);
  }

  @Test
  void testFieldAndAssembly() {
    Class<INodeItem> baseType = INodeItem.class;

    context.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(basicFieldExpr).getStaticResultType();
        will(returnValue(IFieldNodeItem.class));
        allowing(basicAssemblyExpr).getStaticResultType();
        will(returnValue(IAssemblyNodeItem.class));
      }
    });
    @SuppressWarnings("null") Class<? extends INodeItem> result
        = ExpressionUtils.analyzeStaticResultType(baseType, List.of(basicFieldExpr, basicAssemblyExpr));
    assertEquals(IModelNodeItem.class, result);
  }
}
