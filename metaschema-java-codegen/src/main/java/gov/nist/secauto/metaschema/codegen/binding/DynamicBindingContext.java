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

package gov.nist.secauto.metaschema.codegen.binding;

import gov.nist.secauto.metaschema.binding.DefaultBindingContext;
import gov.nist.secauto.metaschema.binding.IBindingMatcher;
import gov.nist.secauto.metaschema.binding.model.IAssemblyClassBinding;
import gov.nist.secauto.metaschema.codegen.IProduction;
import gov.nist.secauto.metaschema.codegen.MetaschemaCompilerHelper;
import gov.nist.secauto.metaschema.model.common.IAssemblyDefinition;
import gov.nist.secauto.metaschema.model.common.IFlagContainer;
import gov.nist.secauto.metaschema.model.common.IMetaschema;
import gov.nist.secauto.metaschema.model.common.util.ObjectUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class DynamicBindingContext
    extends DefaultBindingContext {

  @SuppressWarnings("PMD.UseProperClassLoader") // false positive
  @NonNull
  public static DynamicBindingContext forMetaschema(
      @NonNull IMetaschema metaschema,
      @Nullable Path tempPath) throws IOException {
    Path classDir;
    if (tempPath == null) {
      classDir = Files.createTempDirectory("classes-");
    } else {
      classDir = Files.createTempDirectory(tempPath, "classes-");
    }
    classDir.toFile().deleteOnExit();

    IProduction production = MetaschemaCompilerHelper.compileMetaschema(metaschema, classDir);
    return new DynamicBindingContext(production,
        MetaschemaCompilerHelper.getClassLoader(classDir,
            ObjectUtils.notNull(Thread.currentThread().getContextClassLoader())));
  }

  public DynamicBindingContext(@NonNull IProduction production, ClassLoader classLoader) {
    production.getDefinitionProductionsAsStream()
        .filter(
            definitionProduction -> {
              boolean retval = false;
              IFlagContainer definition = definitionProduction.getDefinition();
              if (definition instanceof IAssemblyDefinition) {
                IAssemblyDefinition assembly = (IAssemblyDefinition) definition;
                if (assembly.isRoot()) {
                  retval = true;
                }
              }
              return retval;
            })
        .map(
            definitionProduction -> {
              IAssemblyDefinition definition = (IAssemblyDefinition) definitionProduction.getDefinition();
              try {
                @SuppressWarnings("unchecked") Class<IAssemblyClassBinding> clazz
                    = ObjectUtils.notNull(
                        (Class<IAssemblyClassBinding>) classLoader
                            .loadClass(
                                definitionProduction.getGeneratedClass().getClassName().reflectionName()));
                return new DynamicBindingMatcher(
                    definition,
                    clazz);
              } catch (ClassNotFoundException ex) {
                throw new IllegalStateException(
                    ex);
              }
            })
        .forEachOrdered(
            matcher -> registerBindingMatcher(
                ObjectUtils.notNull(
                    matcher)));
  }

  private static class DynamicBindingMatcher implements IBindingMatcher {
    private final IAssemblyDefinition definition;
    private final Class<IAssemblyClassBinding> clazz;

    public DynamicBindingMatcher(@NonNull IAssemblyDefinition definition, @NonNull Class<IAssemblyClassBinding> clazz) {
      this.definition = definition;
      this.clazz = clazz;
    }

    protected IAssemblyDefinition getDefinition() {
      return definition;
    }

    protected Class<IAssemblyClassBinding> getClazz() {
      return clazz;
    }

    @SuppressWarnings("null")
    @NonNull
    protected QName getRootQName() {
      return getDefinition().getRootXmlQName();
    }

    @SuppressWarnings("null")
    @NonNull
    protected String getRootJsonName() {
      return getDefinition().getRootJsonName();
    }

    @Override
    public Class<?> getBoundClassForXmlQName(QName rootQName) {
      return getRootQName().equals(
          rootQName) ? getClazz() : null;
    }

    @Override
    public Class<?> getBoundClassForJsonName(String rootName) {
      return getRootJsonName().equals(
          rootName) ? getClazz() : null;
    }

  }
}
