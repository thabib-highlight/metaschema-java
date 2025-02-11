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

import gov.nist.secauto.metaschema.model.common.IAssemblyDefinition;
import gov.nist.secauto.metaschema.model.common.IAssemblyInstance;
import gov.nist.secauto.metaschema.model.common.IChoiceInstance;
import gov.nist.secauto.metaschema.model.common.IDefinition;
import gov.nist.secauto.metaschema.model.common.IFieldDefinition;
import gov.nist.secauto.metaschema.model.common.IFieldInstance;
import gov.nist.secauto.metaschema.model.common.IFlagDefinition;
import gov.nist.secauto.metaschema.model.common.IFlagInstance;
import gov.nist.secauto.metaschema.model.common.IMetaschema;
import gov.nist.secauto.metaschema.model.common.IModelContainer;
import gov.nist.secauto.metaschema.model.common.INamedModelInstance;
import gov.nist.secauto.metaschema.model.common.IRootAssemblyDefinition;
import gov.nist.secauto.metaschema.model.common.util.CollectionUtil;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface INodeItemFactory {
  static INodeItemFactory instance() {
    return DefaultNodeItemFactory.instance();
  }

  @SuppressWarnings("null")
  @NonNull
  default INodeItem newNodeItem(@NonNull IDefinition definition, @NonNull Object value, @NonNull URI baseUri,
      boolean rootNode) {
    INodeItem retval;
    if (definition instanceof IAssemblyDefinition) {
      if (rootNode && definition instanceof IRootAssemblyDefinition) {
        retval = newDocumentNodeItem((IRootAssemblyDefinition) definition, value, baseUri);
      } else {
        retval = newAssemblyNodeItem((IAssemblyDefinition) definition, value, baseUri);
      }
    } else if (definition instanceof IFieldDefinition) {
      retval = newFieldNodeItem((IFieldDefinition) definition, value, baseUri);
    } else {
      throw new UnsupportedOperationException("must be a bound assembly or field");
    }
    return retval;
  }

  @NonNull
  default IFlagNodeItem newFlagNodeItem(@NonNull IFlagDefinition definition, @Nullable URI baseUri) {
    return new FlagDefinitionNodeItemImpl(definition, baseUri);
  }

  @NonNull
  default IFlagNodeItem newFlagNodeItem(@NonNull IFlagInstance instance,
      @NonNull IModelNodeItem parent) {
    return new FlagInstanceNodeItemImpl(instance, parent);
  }

  @NonNull
  default IRequiredValueFlagNodeItem newFlagNodeItem(@NonNull IFlagInstance instance,
      @NonNull IRequiredValueModelNodeItem parent, @NonNull Object value) {
    return new RequiredValueFlagInstanceNodeItemImpl(instance, parent, value);
  }

  @NonNull
  default IFieldNodeItem newFieldNodeItem(@NonNull IFieldDefinition definition, @Nullable URI baseUri) {
    return new FieldDefinitionNodeItemImpl(definition, baseUri, this);
  }

  @NonNull
  default IFieldNodeItem newFieldNodeItem(
      @NonNull IFieldInstance instance,
      @NonNull IAssemblyNodeItem parentNodeItem) {
    return new FieldInstanceNodeItemImpl(instance, parentNodeItem, 1, this);
  }

  @NonNull
  default IRequiredValueFieldNodeItem newFieldNodeItem(@NonNull IFieldDefinition definition,
      @NonNull Object value, @Nullable URI baseUri) {
    return new RequiredValueFieldDefinitionNodeItemImpl(definition, value, baseUri, this);
  }

  @NonNull
  default IRequiredValueFieldNodeItem newFieldNodeItem(@NonNull IFieldInstance instance,
      @NonNull IRequiredValueAssemblyNodeItem parent, int position, @NonNull Object value) {
    return new RequiredValueFieldInstanceNodeItemImpl(instance, parent, position, value, this);
  }

  @NonNull
  default IAssemblyNodeItem newAssemblyNodeItem(@NonNull IAssemblyDefinition definition, @Nullable URI baseUri) {
    return new AssemblyDefinitionNodeItemImpl(definition, baseUri, this);
  }

  @NonNull
  default IAssemblyNodeItem newAssemblyNodeItem(@NonNull IAssemblyInstance instance,
      @NonNull IAssemblyNodeItem parent) {
    return new AssemblyInstanceNodeItemImpl(instance, parent, 1, this);
  }

  default IAssemblyNodeItem newAssemblyNodeItem(@NonNull IAssemblyDefinition definition, @NonNull Object value,
      @Nullable URI baseUri) {
    return new RequiredValueAssemblyDefinitionNodeItemImpl(definition, value, baseUri, this);
  }

  @NonNull
  default IRequiredValueAssemblyNodeItem newAssemblyNodeItem(@NonNull IAssemblyInstance instance,
      @NonNull IRequiredValueAssemblyNodeItem parent, int position, @NonNull Object value) {
    return new RequiredValueAssemblyInstanceNodeItemImpl(instance, parent, position, value, this);
  }

  @NonNull
  default IDocumentNodeItem newDocumentNodeItem(@NonNull IRootAssemblyDefinition definition, @NonNull Object value,
      @NonNull URI documentUri) {
    return new DocumentNodeItemImpl(definition, value, documentUri, this);
  }

  @NonNull
  default IMetaschemaNodeItem newMetaschemaNodeItem(@NonNull IMetaschema metaschema) {
    return new MetaschemaNodeItemImpl(metaschema, this);
  }

  @NonNull
  default Map<String, IFlagNodeItem> generateFlags(@NonNull IModelNodeItem parent) {
    Map<String, IFlagNodeItem> retval = new LinkedHashMap<>(); // NOPMD - intentional

    for (IFlagInstance instance : parent.getDefinition().getFlagInstances()) {
      assert instance != null;
      IFlagNodeItem item = newFlagNodeItem(instance, parent);
      retval.put(instance.getEffectiveName(), item);
    }
    return retval.isEmpty() ? CollectionUtil.emptyMap() : CollectionUtil.unmodifiableMap(retval);
  }

  @NonNull
  default Map<String, IRequiredValueFlagNodeItem> generateFlagsWithValues(
      @NonNull IRequiredValueModelNodeItem parent) {
    Map<String, IRequiredValueFlagNodeItem> retval = new LinkedHashMap<>(); // NOPMD - intentional

    Object parentValue = parent.getValue();
    for (IFlagInstance instance : parent.getDefinition().getFlagInstances()) {
      Object instanceValue = instance.getValue(parentValue);
      if (instanceValue != null) {
        IRequiredValueFlagNodeItem item = newFlagNodeItem(instance, parent, instanceValue);
        retval.put(instance.getEffectiveName(), item);
      }
    }
    return retval.isEmpty() ? CollectionUtil.emptyMap() : CollectionUtil.unmodifiableMap(retval);
  }

  @NonNull
  default Map<String, List<IModelNodeItem>> generateModelItems(@NonNull IAssemblyNodeItem parent) {
    Map<String, List<IModelNodeItem>> retval = new LinkedHashMap<>(); // NOPMD - intentional

    for (INamedModelInstance instance : CollectionUtil.toIterable(getNamedModelInstances(parent.getDefinition()))) {
      @NonNull IModelNodeItem item;
      if (instance instanceof IAssemblyInstance) {
        item = newAssemblyNodeItem((IAssemblyInstance) instance, parent);
      } else if (instance instanceof IFieldInstance) {
        item = newFieldNodeItem((IFieldInstance) instance, parent);
      } else {
        throw new UnsupportedOperationException("unsupported instance type: " + instance.getClass().getName());
      }
      retval.put(instance.getEffectiveName(), Collections.singletonList(item));
    }
    return retval.isEmpty() ? CollectionUtil.emptyMap() : CollectionUtil.unmodifiableMap(retval);
  }

  @SuppressWarnings("null")
  @NonNull
  default Stream<INamedModelInstance> getNamedModelInstances(@NonNull IModelContainer container) {
    return container.getModelInstances().stream()
        .flatMap(instance -> {
          Stream<INamedModelInstance> retval;
          if (instance instanceof IAssemblyInstance || instance instanceof IFieldInstance) {
            retval = Stream.of((INamedModelInstance) instance);
          } else if (instance instanceof IChoiceInstance) {
            // descend into the choice
            retval = getNamedModelInstances((IChoiceInstance) instance);
          } else {
            throw new UnsupportedOperationException("unsupported instance type: " + instance.getClass().getName());
          }
          return retval;
        });
  }

  @NonNull
  default Map<String, List<IRequiredValueModelNodeItem>> generateModelItemsWithValues(
      @NonNull IRequiredValueAssemblyNodeItem parent) {
    Map<String, List<IRequiredValueModelNodeItem>> retval // NOPMD - intentional
        = new LinkedHashMap<>();

    Object parentValue = parent.getValue();
    for (INamedModelInstance instance : CollectionUtil.toIterable(getNamedModelInstances(parent.getDefinition()))) {
      Object instanceValue = instance.getValue(parentValue);

      Stream<? extends Object> itemValues = instance.getItemValues(instanceValue).stream();
      AtomicInteger index = new AtomicInteger(); // NOPMD - intentional
      List<IRequiredValueModelNodeItem> items = itemValues.map(itemValue -> {
        assert itemValue != null;

        @NonNull IRequiredValueModelNodeItem item;
        if (instance instanceof IAssemblyInstance) {
          item = newAssemblyNodeItem((IAssemblyInstance) instance, parent, index.incrementAndGet(),
              itemValue);
        } else if (instance instanceof IFieldInstance) {
          item = newFieldNodeItem((IFieldInstance) instance, parent, index.incrementAndGet(),
              itemValue);
        } else {
          throw new UnsupportedOperationException("unsupported instance type: " + instance.getClass().getName());
        }
        return item;
      }).collect(Collectors.toUnmodifiableList());
      retval.put(instance.getEffectiveName(), items);
    }
    return retval.isEmpty() ? CollectionUtil.emptyMap() : CollectionUtil.unmodifiableMap(retval);
  }
}
