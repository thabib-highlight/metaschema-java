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

import gov.nist.secauto.metaschema.model.common.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.model.common.metapath.function.InvalidValueForCastFunctionException;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IBooleanItem extends IAnyAtomicItem {
  @NonNull
  IBooleanItem TRUE = new BooleanItemImpl(true);
  @NonNull
  IBooleanItem FALSE = new BooleanItemImpl(false);

  @NonNull
  static IBooleanItem valueOf(boolean value) {
    return value ? TRUE : FALSE;
  }

  @NonNull
  static IBooleanItem valueOf(@NonNull Boolean value) {
    return value ? TRUE : FALSE;
  }

  @NonNull
  static IBooleanItem valueOf(@NonNull String value) {
    IBooleanItem retval;
    if ("1".equals(value)) {
      retval = TRUE;
    } else {
      try {
        Boolean bool = MetaschemaDataTypeProvider.BOOLEAN.parse(value);
        retval = valueOf(bool);
      } catch (IllegalArgumentException ex) {
        throw new InvalidValueForCastFunctionException(String.format("Unable to parse string value '%s'", value),
            ex);
      }
    }
    return retval;
  }

  @NonNull
  static IBooleanItem cast(@NonNull IAnyAtomicItem item) throws InvalidValueForCastFunctionException {
    return MetaschemaDataTypeProvider.BOOLEAN.cast(item);
  }

  boolean toBoolean();

  @NonNull
  default IBooleanItem negate() {
    return this.toBoolean() ? FALSE : TRUE;
  }
}
