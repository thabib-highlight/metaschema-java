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

package gov.nist.secauto.metaschema.model.common.datatype.markup.flexmark;

import com.vladsch.flexmark.ast.Emphasis;
import com.vladsch.flexmark.ast.StrongEmphasis;
import com.vladsch.flexmark.parser.core.delimiter.AsteriskDelimiterProcessor;
import com.vladsch.flexmark.parser.core.delimiter.Delimiter;
import com.vladsch.flexmark.parser.core.delimiter.EmphasisDelimiterProcessor;
import com.vladsch.flexmark.parser.delimiter.DelimiterRun;
import com.vladsch.flexmark.util.ast.DelimitedNode;
import com.vladsch.flexmark.util.misc.Utils;
import com.vladsch.flexmark.util.sequence.BasedSequence;

/**
 * Provides a temporary fix for the broken {@link EmphasisDelimiterProcessor} in Flexmark.
 */
public class FixedEmphasisDelimiterProcessor
    extends AsteriskDelimiterProcessor {
  // TODO: remove this class once vsch/flexmark-java#580 is merged
  private final int multipleUse;

  public FixedEmphasisDelimiterProcessor(boolean strongWrapsEmphasis) {
    super(strongWrapsEmphasis);
    this.multipleUse = strongWrapsEmphasis ? 1 : 2;
  }

  @SuppressWarnings("PMD.OnlyOneReturn") // for readability
  @Override
  public int getDelimiterUse(DelimiterRun opener, DelimiterRun closer) {
    // "multiple of 3" rule for internal delimiter runs
    if ((opener.canClose() || closer.canOpen()) && (opener.length() + closer.length()) % 3 == 0) {
      if (opener.length() % 3 == 0 && closer.length() % 3 == 0) {
        return this.multipleUse; // if they are each a multiple of 3, then emphasis can be created
      }
      return 0;
    }

    // calculate actual number of delimiters used from this closer
    if (opener.length() < 3 || closer.length() < 3) {
      return Utils.min(closer.length(), opener.length());
    }
    // default to latest spec
    return closer.length() % 2 == 0 ? 2 : multipleUse;
  }

  @Override
  public void process(Delimiter opener, Delimiter closer, int delimitersUsed) {
    DelimitedNode emphasis = delimitersUsed == 1
        ? new Emphasis(opener.getTailChars(delimitersUsed), BasedSequence.NULL, closer.getLeadChars(delimitersUsed))
        : new StrongEmphasis(opener.getTailChars(delimitersUsed), BasedSequence.NULL,
            closer.getLeadChars(delimitersUsed));

    opener.moveNodesBetweenDelimitersTo(emphasis, closer);
  }

}
