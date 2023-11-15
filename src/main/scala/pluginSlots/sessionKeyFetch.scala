package pluginSlots

import io.simplifier.pluginapi.UserSession
import io.simplifier.session.{RestMessages, SessionManager}
import org.json4s._

import scala.util.Try

case class SessionKeyFetchRequest(sessionid: String, key: String)

case class SessionKeyFetchResponse(success: Boolean, result: JValue, message: String)

/**
 * Slot: Fetch Session Key.
 * @author Christian Simon
 */
trait sessionKeyFetch extends DataOperation[SessionKeyFetchRequest] {

  override def operation(arg: SessionKeyFetchRequest)(implicit userSession: UserSession): Try[Any] = {
    log.debug(s"Fetch Session ${arg.sessionid} / '${arg.key}'")
    SessionManager.getSessionKey(arg.sessionid, arg.key) map {
      case JNothing => SessionKeyFetchResponse(true, JNull, RestMessages.fetchSessionKeySuccess)
      case sessionData => SessionKeyFetchResponse(true, sessionData, RestMessages.fetchSessionKeySuccess)
    }
  }

  override def operationErrorMessage(msg: String): String = RestMessages.fetchSessionKeyError(msg)

  override def parseArgument(item: JValue) = item.extract[SessionKeyFetchRequest]

}

class sessionKeyFetchSlot extends sessionKeyFetch with DataOperationSlot[SessionKeyFetchRequest]

class sessionKeyFetchHttpSlot extends sessionKeyFetch with DataOperationHttpSlot[SessionKeyFetchRequest]
