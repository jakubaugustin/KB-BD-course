package cz.kb.bd.contractregistry

import com.typesafe.config.{ ConfigFactory, Config }
import scala.util.Properties
import org.apache.log4j.{ LogManager, Logger, Level }

/**
 * Checks for argument values in environment variables and configuration file.
 */
object ConfigParser {

  private[this] val log: Logger = LogManager.getRootLogger
  private[this] val config: Config = ConfigFactory.load()

  /**
   * Checks for argument in environment variables and configuration file.
   * If found in environment variable, configuration will not be searched for this argument.
   * For environment variable dots are replaced with underscores
   *
   * @param argumentName name of argument to be returned
   * @param validationRegex optional Regex to validate argument value
   */
  def getArgumentStringValue(argumentName: String, validationRegex: String = ""): String = {
    val argValue: String = Properties.envOrElse(argumentName.toUpperCase.replaceAll("""\.""", "_"), config.getString(argumentName))
    if (validationRegex != "") {
      if (!argValue.matches(validationRegex))
        throw new IllegalStateException(s"Argument validation failed. Argument ${argumentName} with value: ${argValue} did not meet regex: ${validationRegex}")
    }
    log.debug(s"ARGUMENT NAME: ${argumentName}, ARGUMENT VALUE: ${argValue}")
    return argValue
  }

  /**
   * Checks for argument in environment variables and configuration file.
   * If found in environment variable, configuration will not be searched for this argument.
   * @param argumentName name of argument to be returned
   */
  def getArgumentIntValue(argumentName: String): Int = {
    getArgumentStringValue(argumentName).toInt
  }

  /**
   * Checks for argument in environment variables and configuration file.
   * If found in environment variable, configuration will not be searched for this argument.
   * @param argumentName name of argument to be returned
   */
  def getArgumentBooleanValue(argumentName: String): Boolean = {
    getArgumentStringValue(argumentName).toBoolean
  }

}