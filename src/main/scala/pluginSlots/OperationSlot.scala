package pluginSlots

import io.simplifier.pluginapi.UserSession
import io.simplifier.pluginapi.helper.PluginLogger
import io.simplifier.pluginbase.util.json.JSONFormatter.decomposeJSON
import io.simplifier.session.RestMessageException
import org.json4s._

import scala.util.{Failure, Success, Try}

case class OperationError(success: Boolean, message: String)

/**
 * Token for DataOperation type argument for operations that don't require an argument.
 */
sealed trait NoArgument
case object NoArgument extends NoArgument

/**
 * Trait for abstract data operations. Contains the business logic for a data operation
 * and can be plugged into DataOperationHttpSlot and DataOperationSlot.
 */
trait DataOperation[A] extends JsonFormats with PluginLogger {

  def result(item: JValue)(implicit userSession: UserSession): JValue = {
    val result = Try(parseArgument(item)) match {
      case Failure(thr) => {
        log.debug("Error decoding argument: " + item, thr)
        OperationError(false, badRequestMessage)
      }
      case Success(arg) => Try(operation(arg)).flatten match {
        case Failure(RestMessageException(msg)) => OperationError(false, operationErrorMessage(msg))
        case Failure(other) => {
          log.error("Error in operation " + getClass, other)
          OperationError(false, operationErrorMessage("unexpected error"))
        }
        case Success(res) => res
      }
    }
    decomposeJSON(result)
  }

  def operationErrorMessage(msg: String): String

  val badRequestMessage = operationErrorMessage("Unable to parse argument")

  /**
   * Try to parse parameter JSON to parameter class.
   * @param item JSON parameter
   * @return Try wit parsed parameter
   */
  def parseArgument(item: JValue): A

  /**
   * Operation to execute.
   * @param arg parsed parameter
   * @param userSession user session
   * @return operation result
   */
  def operation(arg: A)(implicit userSession: UserSession): Try[Any]

}

/**
 * Abstract HTTP Slot for data model operations.
 * Type parameter [A] is the type of the slot parameter.
 * @author Christian Simon
 */
trait DataOperationHttpSlot[A] extends HttpJsonSlot with DataOperation[A] {

  override def result(item: JValue)(implicit userSession: UserSession): JValue = super[DataOperation].result(item)

}

/**
 * Abstract Slot for data model operations.
 * Type parameter [A] is the type of the slot parameter.
 * @author Christian Simon
 */
trait DataOperationSlot[A] extends JsonSlot with DataOperation[A] {

  override def result(item: JValue)(implicit userSession: UserSession): JValue = super[DataOperation].result(item)

}