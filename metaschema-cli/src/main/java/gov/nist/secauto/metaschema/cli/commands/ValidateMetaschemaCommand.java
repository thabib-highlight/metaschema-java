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

import gov.nist.secauto.metaschema.binding.io.xml.XmlUtil;
import gov.nist.secauto.metaschema.cli.processor.CLIProcessor;
import gov.nist.secauto.metaschema.cli.processor.CLIProcessor.CallingContext;
import gov.nist.secauto.metaschema.cli.processor.ExitCode;
import gov.nist.secauto.metaschema.cli.processor.ExitStatus;
import gov.nist.secauto.metaschema.cli.processor.InvalidArgumentException;
import gov.nist.secauto.metaschema.cli.processor.command.AbstractTerminalCommand;
import gov.nist.secauto.metaschema.cli.processor.command.DefaultExtraArgument;
import gov.nist.secauto.metaschema.cli.processor.command.ExtraArgument;
import gov.nist.secauto.metaschema.cli.processor.command.ICommandExecutor;
import gov.nist.secauto.metaschema.cli.util.LoggingValidationHandler;
import gov.nist.secauto.metaschema.model.MetaschemaLoader;
import gov.nist.secauto.metaschema.model.common.util.CollectionUtil;
import gov.nist.secauto.metaschema.model.common.util.ObjectUtils;
import gov.nist.secauto.metaschema.model.common.validation.IContentValidator;
import gov.nist.secauto.metaschema.model.common.validation.IValidationResult;

import org.apache.commons.cli.CommandLine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.Source;

import edu.umd.cs.findbugs.annotations.NonNull;

public class ValidateMetaschemaCommand
    extends AbstractTerminalCommand {
  private static final Logger LOGGER = LogManager.getLogger(ValidateMetaschemaCommand.class);
  @NonNull
  private static final String COMMAND = "validate";
  @NonNull
  private static final List<ExtraArgument> EXTRA_ARGUMENTS;

  static {
    EXTRA_ARGUMENTS = ObjectUtils.notNull(List.of(
        new DefaultExtraArgument("Metaschema file to validate", true)));
  }

  @Override
  public String getName() {
    return COMMAND;
  }

  @Override
  public String getDescription() {
    return "Validate that the specified Metaschema is well-formed and valid to the Metaschema model";
  }

  @Override
  public List<ExtraArgument> getExtraArguments() {
    return EXTRA_ARGUMENTS;
  }

  @NonNull
  protected List<Source> getXmlSchemaSources() throws IOException {
    List<Source> retval = new LinkedList<>();
    retval.add(XmlUtil.getStreamSource(
        MetaschemaLoader.class.getResource("/schema/xml/metaschema.xsd")));
    return CollectionUtil.unmodifiableList(retval);
  }

  @Override
  public void validateOptions(CallingContext callingContext, CommandLine cmdLine) throws InvalidArgumentException {
    List<String> extraArgs = cmdLine.getArgList();
    if (extraArgs.size() != 1) {
      throw new InvalidArgumentException("The source to validate must be provided.");
    }

    File target = new File(extraArgs.get(0));
    if (!target.exists()) {
      throw new InvalidArgumentException("The provided source file '" + target.getPath() + "' does not exist.");
    }
    if (!target.canRead()) {
      throw new InvalidArgumentException("The provided source file '" + target.getPath() + "' is not readable.");
    }
  }

  @Override
  public ICommandExecutor newExecutor(CallingContext callingContext, CommandLine cmdLine) {
    return ICommandExecutor.using(callingContext, cmdLine, this::executeCommand);
  }

  @SuppressWarnings("PMD.OnlyOneReturn") // readability
  @NonNull
  protected ExitStatus executeCommand(CallingContext callingContext, CommandLine cmdLine) {
    List<String> extraArgs = cmdLine.getArgList();
    Path target = Paths.get(extraArgs.get(0));
    assert target != null;

    IValidationResult schemaValidationResult;
    try {
      List<Source> schemaSources = getXmlSchemaSources();
      schemaValidationResult = IContentValidator.validateWithXmlSchema(target, schemaSources);
    } catch (IOException | SAXException ex) {
      return ExitCode.PROCESSING_ERROR.exit().withThrowable(ex);
    }

    if (!schemaValidationResult.isPassing()) {
      if (LOGGER.isErrorEnabled()) {
        LOGGER.error("The file '{}' has validation issue(s). The issues are:", target);
      }
      LoggingValidationHandler.instance().handleValidationResults(schemaValidationResult);
      return ExitCode.FAIL.exit();
    }

    if (schemaValidationResult.isPassing() && !cmdLine.hasOption(CLIProcessor.QUIET_OPTION) && LOGGER.isInfoEnabled()) {
      LOGGER.info("The file '{}' is valid.", target);
    }
    return ExitCode.OK.exit();
  }
}
