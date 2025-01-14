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

package gov.nist.secauto.metaschema.cli.commands;

import gov.nist.secauto.metaschema.binding.IBindingContext;
import gov.nist.secauto.metaschema.binding.io.xml.XmlUtil;
import gov.nist.secauto.metaschema.cli.processor.CLIProcessor.CallingContext;
import gov.nist.secauto.metaschema.cli.processor.InvalidArgumentException;
import gov.nist.secauto.metaschema.cli.processor.command.ICommandExecutor;
import gov.nist.secauto.metaschema.codegen.binding.DynamicBindingContext;
import gov.nist.secauto.metaschema.model.MetaschemaLoader;
import gov.nist.secauto.metaschema.model.common.IMetaschema;
import gov.nist.secauto.metaschema.model.common.MetaschemaException;
import gov.nist.secauto.metaschema.model.common.configuration.DefaultConfiguration;
import gov.nist.secauto.metaschema.model.common.configuration.IMutableConfiguration;
import gov.nist.secauto.metaschema.model.common.constraint.IConstraintSet;
import gov.nist.secauto.metaschema.model.common.util.CollectionUtil;
import gov.nist.secauto.metaschema.model.common.util.ObjectUtils;
import gov.nist.secauto.metaschema.model.common.validation.JsonSchemaContentValidator;
import gov.nist.secauto.metaschema.schemagen.ISchemaGenerator;
import gov.nist.secauto.metaschema.schemagen.ISchemaGenerator.SchemaFormat;
import gov.nist.secauto.metaschema.schemagen.SchemaGenerationFeature;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.xml.transform.Source;

import edu.umd.cs.findbugs.annotations.NonNull;

public class ValidateContentWithMetaschemaCommand
    extends AbstractValidateContentCommand {
  @NonNull
  private static final String COMMAND = "validate-content";

  @Override
  public String getName() {
    return COMMAND;
  }

  @Override
  public String getDescription() {
    return "Verify that the provided resource is well-formed and valid to the provided Metaschema-based model.";
  }

  @Override
  public Collection<? extends Option> gatherOptions() {
    Collection<? extends Option> orig = super.gatherOptions();

    List<Option> retval = new ArrayList<>(orig.size() + 1);
    retval.addAll(orig);
    retval.add(MetaschemaCommandSupport.METASCHEMA_OPTION);

    return CollectionUtil.unmodifiableCollection(retval);
  }

  @Override
  public void validateOptions(CallingContext callingContext, CommandLine cmdLine) throws InvalidArgumentException {
    super.validateOptions(callingContext, cmdLine);

    String metaschemaName = cmdLine.getOptionValue(MetaschemaCommandSupport.METASCHEMA_OPTION);
    Path metaschema = Paths.get(metaschemaName);
    if (!Files.exists(metaschema)) {
      throw new InvalidArgumentException("The provided metaschema '" + metaschema + "' does not exist.");
    }
    if (!Files.isReadable(metaschema)) {
      throw new InvalidArgumentException("The provided metaschema '" + metaschema + "' is not readable.");
    }
  }

  @Override
  public ICommandExecutor newExecutor(CallingContext callingContext, CommandLine commandLine) {
    return new OscalCommandExecutor(callingContext, commandLine);
  }

  private class OscalCommandExecutor
      extends AbstractValidationCommandExecutor {

    private Path tempDir;
    private IMetaschema metaschema;

    private OscalCommandExecutor(
        @NonNull CallingContext callingContext,
        @NonNull CommandLine commandLine) {
      super(callingContext, commandLine);
    }

    private Path getTempDir() throws IOException {
      if (tempDir == null) {
        tempDir = Files.createTempDirectory("validation-");
        tempDir.toFile().deleteOnExit();
      }
      return tempDir;
    }

    @NonNull
    private IMetaschema getMetaschema(@NonNull Set<IConstraintSet> constraintSets)
        throws MetaschemaException, IOException {
      if (metaschema == null) {
        String metaschemaName = getCommandLine().getOptionValue(MetaschemaCommandSupport.METASCHEMA_OPTION);
        Path metaschemaPath = Paths.get(metaschemaName);
        assert metaschemaPath != null;

        MetaschemaLoader loader = new MetaschemaLoader(constraintSets);
        loader.allowEntityResolution();
        metaschema = loader.load(metaschemaPath);
      }
      assert metaschema != null;
      return metaschema;
    }

    @NonNull
    private IMetaschema getMetaschema() {
      // should be initialized already
      return ObjectUtils.requireNonNull(metaschema);
    }

    @Override
    protected IBindingContext getBindingContext(@NonNull Set<IConstraintSet> constraintSets)
        throws MetaschemaException, IOException {

      return DynamicBindingContext.forMetaschema(getMetaschema(constraintSets), getTempDir());
    }

    @Override
    public List<Source> getXmlSchemas() throws IOException {
      Path schemaFile = Files.createTempFile(getTempDir(), "schema-", ".json");
      assert schemaFile != null;
      IMutableConfiguration<SchemaGenerationFeature<?>> configuration = new DefaultConfiguration<>();
      ISchemaGenerator.generateSchema(getMetaschema(), schemaFile, SchemaFormat.XML, configuration);
      return ObjectUtils.requireNonNull(List.of(
          XmlUtil.getStreamSource(schemaFile.toUri().toURL())));
    }

    @Override
    public JSONObject getJsonSchema() throws IOException {
      Path schemaFile = Files.createTempFile(getTempDir(), "schema-", ".json");
      assert schemaFile != null;
      IMutableConfiguration<SchemaGenerationFeature<?>> configuration = new DefaultConfiguration<>();
      ISchemaGenerator.generateSchema(getMetaschema(), schemaFile, SchemaFormat.JSON, configuration);
      try (BufferedReader reader = ObjectUtils.notNull(Files.newBufferedReader(schemaFile, StandardCharsets.UTF_8))) {
        return JsonSchemaContentValidator.toJsonObject(reader);
      }
    }
  }

}
