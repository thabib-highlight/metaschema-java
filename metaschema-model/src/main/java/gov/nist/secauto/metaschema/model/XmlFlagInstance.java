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

package gov.nist.secauto.metaschema.model;

import gov.nist.secauto.metaschema.model.common.AbstractFlagInstance;
import gov.nist.secauto.metaschema.model.common.IFlagContainer;
import gov.nist.secauto.metaschema.model.common.IFlagDefinition;
import gov.nist.secauto.metaschema.model.common.MetaschemaModelConstants;
import gov.nist.secauto.metaschema.model.common.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.model.common.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.model.common.util.CollectionUtil;
import gov.nist.secauto.metaschema.model.common.util.ObjectUtils;
import gov.nist.secauto.metaschema.model.xmlbeans.FlagReferenceType;

import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

class XmlFlagInstance
    extends AbstractFlagInstance {
  @NonNull
  private final FlagReferenceType xmlFlag;

  /**
   * Constructs a new Metaschema flag instance definition from an XML representation bound to Java
   * objects.
   *
   * @param xmlFlag
   *          the XML representation bound to Java objects
   * @param parent
   *          the field definition this object is an instance of
   */
  public XmlFlagInstance(@NonNull FlagReferenceType xmlFlag, @NonNull IFlagContainer parent) {
    super(parent);
    this.xmlFlag = xmlFlag;
  }

  /**
   * Get the underlying XML data.
   *
   * @return the underlying XML data
   */
  protected FlagReferenceType getXmlFlag() {
    return xmlFlag;
  }

  @Override
  public IFlagDefinition getDefinition() {
    // this will always be not null
    return ObjectUtils.notNull(getContainingDefinition().getContainingMetaschema()
        .getScopedFlagDefinitionByName(getName()));
  }

  @Override
  public String getFormalName() {
    return getXmlFlag().isSetFormalName() ? getXmlFlag().getFormalName() : null;
  }

  @SuppressWarnings("null")
  @Override
  public MarkupLine getDescription() {
    return getXmlFlag().isSetDescription() ? MarkupStringConverter.toMarkupString(getXmlFlag().getDescription()) : null;
  }

  @Override
  public Map<QName, Set<String>> getProperties() {
    return ModelFactory.toProperties(CollectionUtil.listOrEmpty(getXmlFlag().getPropList()));
  }

  @SuppressWarnings("null")
  @Override
  public String getName() {
    return getXmlFlag().getRef();
  }

  @Override
  public String getXmlNamespace() {
    return null;
  }

  @SuppressWarnings("null")
  @Override
  public MarkupMultiline getRemarks() {
    return getXmlFlag().isSetRemarks() ? MarkupStringConverter.toMarkupString(getXmlFlag().getRemarks()) : null;
  }

  @Override
  public boolean isRequired() {
    return getXmlFlag().isSetRequired() ? getXmlFlag().getRequired() : MetaschemaModelConstants.DEFAULT_FLAG_REQUIRED;
  }

  @Override
  public String getUseName() {
    return getXmlFlag().isSetUseName() ? getXmlFlag().getUseName() : null;
  }

  @Override
  public Object getValue(@NonNull Object parentValue) {
    // there is no value
    return null;
  }
}
