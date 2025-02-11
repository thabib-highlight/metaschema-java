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

import gov.nist.secauto.metaschema.model.common.IAssemblyDefinition;
import gov.nist.secauto.metaschema.model.common.IFieldDefinition;
import gov.nist.secauto.metaschema.model.common.IFlagContainer;
import gov.nist.secauto.metaschema.model.common.IFlagInstance;
import gov.nist.secauto.metaschema.model.common.util.CollectionUtil;
import gov.nist.secauto.metaschema.model.xmlbeans.FlagReferenceType;
import gov.nist.secauto.metaschema.model.xmlbeans.GlobalAssemblyDefinitionType;
import gov.nist.secauto.metaschema.model.xmlbeans.GlobalFieldDefinitionType;
import gov.nist.secauto.metaschema.model.xmlbeans.InlineAssemblyDefinitionType;
import gov.nist.secauto.metaschema.model.xmlbeans.InlineFieldDefinitionType;
import gov.nist.secauto.metaschema.model.xmlbeans.InlineFlagDefinitionType;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

class XmlFlagContainerSupport {

  @NonNull
  private final Map<String, IFlagInstance> flagInstances;

  /**
   * Generate a set of constraints from the provided XMLBeans instance.
   *
   * @param xmlField
   *          the XMLBeans instance
   * @param container
   *          the field containing the flag
   */
  public XmlFlagContainerSupport(
      @NonNull GlobalFieldDefinitionType xmlField,
      @NonNull IFieldDefinition container) {
    // handle flags
    if (xmlField.getFlagList().size() > 0 || xmlField.getDefineFlagList().size() > 0) {
      this.flagInstances = parseLocalFlags(xmlField, container);
    } else {
      this.flagInstances = CollectionUtil.emptyMap();
    }
  }

  /**
   * Generate a set of constraints from the provided XMLBeans instance.
   *
   * @param xmlField
   *          the XMLBeans instance
   * @param container
   *          the field containing the flag
   */
  public XmlFlagContainerSupport(
      @NonNull InlineFieldDefinitionType xmlField,
      @NonNull IFieldDefinition container) {
    // handle flags
    if (xmlField.getFlagList().size() > 0 || xmlField.getDefineFlagList().size() > 0) {
      this.flagInstances = parseLocalFlags(xmlField, container);
    } else {
      this.flagInstances = CollectionUtil.emptyMap();
    }
  }

  /**
   * Generate a set of constraints from the provided XMLBeans instance.
   *
   * @param xmlAssembly
   *          the XMLBeans instance
   * @param container
   *          the assembly containing the flag
   */
  public XmlFlagContainerSupport(
      @NonNull GlobalAssemblyDefinitionType xmlAssembly,
      @NonNull IAssemblyDefinition container) {
    // handle flags
    if (xmlAssembly.getFlagList().size() > 0 || xmlAssembly.getDefineFlagList().size() > 0) {
      this.flagInstances = parseLocalFlags(xmlAssembly, container);
    } else {
      this.flagInstances = CollectionUtil.emptyMap();
    }
  }

  /**
   * Generate a set of constraints from the provided XMLBeans instance.
   *
   * @param xmlAssembly
   *          the XMLBeans instance
   * @param container
   *          the assembly containing the flag
   */
  public XmlFlagContainerSupport(
      @NonNull InlineAssemblyDefinitionType xmlAssembly,
      @NonNull IAssemblyDefinition container) {
    // handle flags
    if (xmlAssembly.getFlagList().size() > 0 || xmlAssembly.getDefineFlagList().size() > 0) {
      this.flagInstances = parseLocalFlags(xmlAssembly, container);
    } else {
      this.flagInstances = CollectionUtil.emptyMap();
    }
  }

  /**
   * Get a mapping of flag effective name to flag instance.
   *
   * @return the mapping of flag effective name to flag instance
   */
  @NonNull
  public Map<String, ? extends IFlagInstance> getFlagInstanceMap() {
    return flagInstances;
  }

  @NonNull
  private static Map<String, IFlagInstance> parseLocalFlags(@NonNull XmlObject xmlObject,
      @NonNull IFlagContainer parent) {
    // handle flags
    Map<String, IFlagInstance> flagInstances = new LinkedHashMap<>(); // NOPMD - intentional
    try (XmlCursor cursor = xmlObject.newCursor()) {
      cursor.selectPath(
          "declare namespace m='http://csrc.nist.gov/ns/oscal/metaschema/1.0';" + "$this/m:flag|$this/m:define-flag");

      while (cursor.toNextSelection()) {
        XmlObject obj = cursor.getObject();
        if (obj instanceof FlagReferenceType) {
          XmlFlagInstance flagInstance = new XmlFlagInstance((FlagReferenceType) obj, parent); // NOPMD - intentional
          flagInstances.put(flagInstance.getEffectiveName(), flagInstance);
        } else if (obj instanceof InlineFlagDefinitionType) {
          XmlInlineFlagDefinition flagInstance
              = new XmlInlineFlagDefinition((InlineFlagDefinitionType) obj, parent); // NOPMD - intentional
          flagInstances.put(flagInstance.getEffectiveName(), flagInstance);
        }
      }
    }

    @SuppressWarnings("null")
    @NonNull Map<String, IFlagInstance> retval
        = flagInstances.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(flagInstances);
    return retval;
  }
}
