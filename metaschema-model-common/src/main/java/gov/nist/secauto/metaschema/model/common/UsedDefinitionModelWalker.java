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

package gov.nist.secauto.metaschema.model.common;

import gov.nist.secauto.metaschema.model.common.util.CollectionUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * This model walker can be used to gather metaschema definitions that are defined globally.
 */
public class UsedDefinitionModelWalker
    extends DefinitionCollectingModelWalker {
  private static final Function<IDefinition, Boolean> FILTER = (def) -> {
    return true;
    // return def.isGlobal();
    // return def.isGlobal() || (def instanceof AssemblyDefinition &&
    // ((AssemblyDefinition)def).getRootName() != null);
  };

  /**
   * Get the collection of all definitions used directly and transitively by the provided definitions.
   *
   * @param definitions
   *          a collection of definitions to generate used definitions from
   * @return the collection of used definitions
   */
  @NonNull
  public static Collection<? extends IDefinition>
      collectUsedDefinitions(Collection<? extends IAssemblyDefinition> definitions) {
    UsedDefinitionModelWalker walker = new UsedDefinitionModelWalker();
    for (IAssemblyDefinition definition : definitions) {
      assert definition != null;
      walker.walk(definition);
    }
    return walker.getDefinitions();
  }

  /**
   * Collect the globally defined metaschema definitions from the provided metaschemas, and any
   * metaschema imported by these metaschema.
   *
   * @param metaschemas
   *          the metaschemas to analyze
   * @return a collection of matching definitions
   */
  @NonNull
  public static Collection<? extends IDefinition>
      collectUsedDefinitionsFromMetaschema(@NonNull Collection<? extends IMetaschema> metaschemas) {
    Set<IAssemblyDefinition> definitions = new HashSet<>();
    for (IMetaschema metaschema : metaschemas) {
      // get local roots in case they are scope=local
      for (IAssemblyDefinition rootDef : metaschema.getRootAssemblyDefinitions()) {
        definitions.add(rootDef);
      }

      // get roots from exported
      for (IAssemblyDefinition assembly : metaschema.getExportedAssemblyDefinitions()) {
        if (assembly.isRoot()) {
          definitions.add(assembly);
        }
      }
    }
    return collectUsedDefinitions(definitions);
  }

  /**
   * Collect the globally defined metaschema definitions from the provided metaschema, and any
   * metaschema imported by this metaschema.
   *
   * @param metaschema
   *          the metaschema to analyze
   * @return a collection of matching definitions
   */
  @NonNull
  public static Collection<? extends IDefinition>
      collectUsedDefinitionsFromMetaschema(@NonNull IMetaschema metaschema) {
    return collectUsedDefinitionsFromMetaschema(CollectionUtil.singleton(metaschema));
  }

  /**
   * Construct a new walker.
   */
  protected UsedDefinitionModelWalker() {
    super(FILTER);
  }
}
