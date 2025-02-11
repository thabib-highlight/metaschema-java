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

package gov.nist.secauto.metaschema.binding.io.xml;

import com.ctc.wstx.stax.WstxInputFactory;

import gov.nist.secauto.metaschema.binding.IBindingContext;
import gov.nist.secauto.metaschema.binding.io.AbstractDeserializer;
import gov.nist.secauto.metaschema.binding.model.IAssemblyClassBinding;
import gov.nist.secauto.metaschema.binding.model.RootAssemblyDefinition;
import gov.nist.secauto.metaschema.model.common.metapath.item.DefaultNodeItemFactory;
import gov.nist.secauto.metaschema.model.common.metapath.item.IDocumentNodeItem;
import gov.nist.secauto.metaschema.model.common.util.AutoCloser;
import gov.nist.secauto.metaschema.model.common.util.ObjectUtils;

import org.codehaus.stax2.XMLEventReader2;
import org.codehaus.stax2.XMLInputFactory2;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;

import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import edu.umd.cs.findbugs.annotations.NonNull;

public class DefaultXmlDeserializer<CLASS>
    extends AbstractDeserializer<CLASS> {
  private XMLInputFactory2 xmlInputFactory;

  public DefaultXmlDeserializer(@NonNull IBindingContext bindingContext, @NonNull IAssemblyClassBinding classBinding) {
    super(bindingContext, classBinding);
  }

  // @Override
  // public Format supportedFromat() {
  // return Format.XML;
  // }

  @NonNull
  protected XMLInputFactory2 getXMLInputFactory() {
    synchronized (this) {
      if (xmlInputFactory == null) {
        xmlInputFactory = (XMLInputFactory2) XMLInputFactory.newInstance();
        assert xmlInputFactory instanceof WstxInputFactory;
        xmlInputFactory.configureForXmlConformance();
        xmlInputFactory.setProperty(XMLInputFactory.IS_COALESCING, false);
        // xmlInputFactory.configureForSpeed();
      }
      return ObjectUtils.notNull(xmlInputFactory);
    }
  }

  protected void setXMLInputFactory(@NonNull XMLInputFactory2 factory) {
    synchronized (this) {
      this.xmlInputFactory = factory;
    }
  }

  @NonNull
  protected XMLEventReader2 newXMLEventReader2(@NonNull Reader reader) throws XMLStreamException {
    XMLEventReader eventReader = getXMLInputFactory().createXMLEventReader(reader);
    EventFilter filter = new CommentFilter();
    return ObjectUtils.notNull((XMLEventReader2) getXMLInputFactory().createFilteredReader(eventReader, filter));
  }

  @Override
  protected IDocumentNodeItem deserializeToNodeItemInternal(Reader reader, URI documentUri) throws IOException {
    // doesn't auto close the underlying reader
    try (AutoCloser<XMLEventReader2, XMLStreamException> closer
        = new AutoCloser<>(newXMLEventReader2(reader), event -> event.close())) {
      return parseXmlInternal(closer.getResource(), documentUri);
    } catch (XMLStreamException ex) {
      throw new IOException("Unable to create a new XMLEventReader2 instance.", ex);
    }
  }

  @NonNull
  protected IDocumentNodeItem parseXmlInternal(@NonNull XMLEventReader2 reader, @NonNull URI documentUri)
      throws IOException, XMLStreamException {

    IAssemblyClassBinding classBinding = getClassBinding();
    if (!classBinding.isRoot()) {
      throw new IOException(
          String.format("The assembly '%s' is not a root assembly.", classBinding.getBoundClass().getName()));
    }

    DefaultXmlParsingContext parsingContext = new DefaultXmlParsingContext(reader, new DefaultXmlProblemHandler());

    RootAssemblyDefinition root = new RootAssemblyDefinition(classBinding);

    return DefaultNodeItemFactory.instance().newDocumentNodeItem(root, root.readRoot(parsingContext), documentUri);
  }
}
