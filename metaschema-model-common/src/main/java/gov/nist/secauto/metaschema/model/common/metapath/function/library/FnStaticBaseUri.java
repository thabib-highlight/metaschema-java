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

package gov.nist.secauto.metaschema.model.common.metapath.function.library;

import gov.nist.secauto.metaschema.model.common.metapath.DynamicContext;
import gov.nist.secauto.metaschema.model.common.metapath.ISequence;
import gov.nist.secauto.metaschema.model.common.metapath.function.IArgument;
import gov.nist.secauto.metaschema.model.common.metapath.function.IFunction;
import gov.nist.secauto.metaschema.model.common.metapath.item.IAnyUriItem;
import gov.nist.secauto.metaschema.model.common.metapath.item.INodeItem;
import gov.nist.secauto.metaschema.model.common.metapath.item.IStringItem;

import java.net.URI;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public final class FnStaticBaseUri {
  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name("static-base-uri")
      .argument(IArgument.newBuilder()
          .name("arg1")
          .type(IStringItem.class)
          .zeroOrOne()
          .build())
      .returnType(IAnyUriItem.class)
      .returnOne()
      .functionHandler(FnStaticBaseUri::execute)
      .build();

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IAnyUriItem> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      INodeItem focus) {

    IAnyUriItem uri = fnStaticBaseUri(dynamicContext);
    return ISequence.of(uri);
  }

  private FnStaticBaseUri() {
    // disable construction
  }

  /**
   * Get the static base URI from the static context.
   *
   * @param context
   *          the dynamic context
   * @return the base URI or {@code null} if none was set
   */
  @Nullable
  public static IAnyUriItem fnStaticBaseUri(@NonNull DynamicContext context) {
    URI staticBaseUri = context.getStaticContext().getBaseUri();

    return staticBaseUri == null ? null : IAnyUriItem.valueOf(staticBaseUri);
  }
}
