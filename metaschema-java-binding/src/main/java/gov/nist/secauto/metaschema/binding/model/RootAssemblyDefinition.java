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

package gov.nist.secauto.metaschema.binding.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import gov.nist.secauto.metaschema.binding.IBindingContext;
import gov.nist.secauto.metaschema.binding.io.BindingException;
import gov.nist.secauto.metaschema.binding.io.json.IJsonParsingContext;
import gov.nist.secauto.metaschema.binding.io.json.IJsonWritingContext;
import gov.nist.secauto.metaschema.binding.io.json.JsonUtil;
import gov.nist.secauto.metaschema.binding.io.xml.IXmlParsingContext;
import gov.nist.secauto.metaschema.binding.io.xml.IXmlWritingContext;
import gov.nist.secauto.metaschema.binding.model.annotations.MetaschemaAssembly;
import gov.nist.secauto.metaschema.model.common.RootAssemblyDefinitionWrapper;
import gov.nist.secauto.metaschema.model.common.util.CollectionUtil;
import gov.nist.secauto.metaschema.model.common.util.ObjectUtils;
import gov.nist.secauto.metaschema.model.common.util.XmlEventUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.stax2.XMLEventReader2;
import org.codehaus.stax2.XMLStreamWriter2;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import edu.umd.cs.findbugs.annotations.NonNull;

public class RootAssemblyDefinition
    extends RootAssemblyDefinitionWrapper<IAssemblyClassBinding>
    implements IRootAssemblyClassBinding {
  private static final Logger LOGGER = LogManager.getLogger(RootAssemblyDefinition.class);

  public RootAssemblyDefinition(@NonNull IAssemblyClassBinding rootDefinition) {
    super(rootDefinition);
  }

  @Override
  public Object readObject(@NonNull IJsonParsingContext context) throws IOException {
    return getRootDefinition().readObject(context);
  }

  @Override
  public IBindingContext getBindingContext() {
    return getRootDefinition().getBindingContext();
  }

  @Override
  public Class<?> getBoundClass() {
    return getRootDefinition().getBoundClass();
  }

  @Override
  public IBoundFlagInstance getJsonKeyFlagInstance() {
    // always null, since this is a root
    return null;
  }

  @Override
  public Map<String, ? extends IBoundNamedInstance>
      getNamedInstances(Predicate<IBoundFlagInstance> flagFilter) {
    return getRootDefinition().getNamedInstances(flagFilter);
  }

  @Override
  public List<Object> readItem(Object parentInstance, boolean requiresJsonKey, IJsonParsingContext context)
      throws IOException {
    return getRootDefinition().readItem(parentInstance, requiresJsonKey, context);
  }

  @Override
  public Object readItem(Object parentInstance, StartElement start, IXmlParsingContext context)
      throws IOException, XMLStreamException {
    return getRootDefinition().readItem(parentInstance, start, context);
  }

  @Override
  public void writeItem(Object item, QName parentName, IXmlWritingContext context)
      throws IOException, XMLStreamException {
    getRootDefinition().writeItem(item, parentName, context);
  }

  @Override
  public void writeItems(Collection<? extends Object> items, boolean writeObjectWrapper,
      IJsonWritingContext context) throws IOException {
    getRootDefinition().writeItems(items, writeObjectWrapper, context);
  }

  @Override
  public Object copyBoundObject(Object item, Object parentInstance) throws BindingException {
    return getRootDefinition().copyBoundObject(item, parentInstance);
  }

  @Override
  public IBoundFlagInstance getFlagInstanceByName(String name) {
    return getRootDefinition().getFlagInstanceByName(name);
  }

  @Override
  public Collection<? extends IBoundFlagInstance> getFlagInstances() {
    return getRootDefinition().getFlagInstances();
  }

  @Override
  public Collection<? extends IBoundNamedModelInstance> getModelInstances() {
    return getRootDefinition().getModelInstances();
  }

  @Override
  public Collection<? extends IBoundNamedModelInstance> getNamedModelInstances() {
    return getRootDefinition().getNamedModelInstances();
  }

  @Override
  public IBoundNamedModelInstance getModelInstanceByName(String name) {
    return getRootDefinition().getModelInstanceByName(name);
  }

  @Override
  public Collection<? extends IBoundFieldInstance> getFieldInstances() {
    return getRootDefinition().getFieldInstances();
  }

  @Override
  public IBoundFieldInstance getFieldInstanceByName(String name) {
    return getRootDefinition().getFieldInstanceByName(name);
  }

  @Override
  public Collection<? extends IBoundAssemblyInstance> getAssemblyInstances() {
    return getRootDefinition().getAssemblyInstances();
  }

  @Override
  public IBoundAssemblyInstance getAssemblyInstanceByName(String name) {
    return getRootDefinition().getAssemblyInstanceByName(name);
  }

  // TODO: this is unused, remove it
  @Override
  public Object readRoot(IXmlParsingContext context) throws IOException, XMLStreamException {

    XMLEventReader2 reader = context.getReader();

    // we may be at the START_DOCUMENT
    if (reader.peek().isStartDocument()) {
      XmlEventUtil.consumeAndAssert(reader, XMLStreamConstants.START_DOCUMENT);
    }

    XmlEventUtil.skipEvents(reader, XMLStreamConstants.CHARACTERS, XMLStreamConstants.PROCESSING_INSTRUCTION);

    QName rootQName = getRootXmlQName();
    if (!reader.peek().isStartElement()) {
      throw new IOException(
          String.format("Expected an element named '%s', but found a '%s' instead.",
              rootQName,
              XmlEventUtil.toString(reader.peek())));
    }

    XmlEventUtil.assertNext(reader, XMLStreamConstants.START_ELEMENT, rootQName);

    StartElement start = ObjectUtils.notNull(reader.nextEvent().asStartElement());
    Object result = ObjectUtils.requireNonNull(readItem(null, start, context));

    XmlEventUtil.consumeAndAssert(reader, XMLStreamConstants.END_ELEMENT, rootQName);

    // if (reader.hasNext() && LOGGER.isDebugEnabled()) {
    // LOGGER.debug("After Parse: {}", XmlEventUtil.toString(reader.peek()));
    // }

    return result;
  }

  @SuppressWarnings("resource") // not owned
  @Override
  public Object readRoot(IJsonParsingContext context) throws IOException {
    if (!isRoot()) {
      throw new IOException(
          String.format("The bound assembly '%s' does not have a root defined in the '%s' annotation.",
              getBoundClass().getName(),
              MetaschemaAssembly.class.getName()));
    }

    JsonParser parser = context.getReader(); // NOPMD - intentional

    boolean objectWrapper = false;
    if (parser.currentToken() == null) {
      parser.nextToken();
    }

    if (JsonToken.START_OBJECT.equals(parser.currentToken())) {
      // advance past the start object to the field name
      JsonUtil.assertAndAdvance(parser, JsonToken.START_OBJECT);
      objectWrapper = true;
    }

    String rootFieldName = getRootJsonName();
    JsonToken token;
    Object instance = null;
    while (!(JsonToken.END_OBJECT.equals(token = parser.currentToken()) || token == null)) {
      if (!JsonToken.FIELD_NAME.equals(token)) {
        throw new IOException(String.format("Expected FIELD_NAME token, found '%s'", token.toString()));
      }

      String fieldName = parser.currentName();
      if (fieldName.equals(rootFieldName)) {
        // process the object value, bound to the requested class
        JsonUtil.assertAndAdvance(parser, JsonToken.FIELD_NAME);
        instance = readObject(context);

        // stop now, since we found the root field
        break;
      }

      if (!context.getProblemHandler().handleUnknownRootProperty(this, fieldName, context)) {
        LOGGER.warn("Skipping unhandled top-level JSON field '{}'.", fieldName);
        JsonUtil.skipNextValue(parser);
      }
    }

    if (instance == null) {
      throw new IOException(String.format("Failed to find root field '%s'.", rootFieldName));
    }

    if (objectWrapper) {
      // advance past the end object
      JsonUtil.assertAndAdvance(parser, JsonToken.END_OBJECT);
    }

    return instance;
  }

  @Override
  public void writeRoot(Object instance, IXmlWritingContext context) throws XMLStreamException, IOException {

    XMLStreamWriter2 writer = context.getWriter();

    writer.writeStartDocument("UTF-8", "1.0");

    QName rootQName = getRootXmlQName();

    NamespaceContext nsContext = writer.getNamespaceContext();
    String prefix = nsContext.getPrefix(rootQName.getNamespaceURI());
    if (prefix == null) {
      prefix = "";
    }

    writer.writeStartElement(prefix, rootQName.getLocalPart(), rootQName.getNamespaceURI());

    writeItem(instance, rootQName, context);

    writer.writeEndElement();
  }

  @Override
  public void writeRoot(Object instance, IJsonWritingContext context) throws IOException {

    @SuppressWarnings("resource") JsonGenerator writer = context.getWriter(); // NOPMD - intentional

    // first read the initial START_OBJECT
    writer.writeStartObject();

    writer.writeFieldName(getRootJsonName());

    writeItems(CollectionUtil.singleton(instance), true, context);

    // end of root object
    writer.writeEndObject();
  }

}
