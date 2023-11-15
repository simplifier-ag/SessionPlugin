package pluginSlots

import io.simplifier.pluginapi.UserSession
import io.simplifier.session.{RestMessages, SessionManager}
import org.json4s._

import scala.util.Try

case class SessionKeyDeleteRequest(sessionid: String, key: String)

case class SessionKeyDeleteResponse(success: Boolean, message: String)

/**
 * Slot: Delete Session Key.
 * @author Christian Simon
 */
trait sessionKeyDelete extends DataOperation[SessionKeyDeleteRequest] {

  override def operation(arg: SessionKeyDeleteRequest)(implicit userSession: UserSession): Try[Any] = {
    log.debug(s"Delete Session ${arg.sessionid} / '${arg.key}'")
    SessionManager.deleteSessionKey(arg.sessionid, arg.key) map {
      session => SessionKeyDeleteResponse(true, RestMessages.deleteSessionKeySuccess)
    }
  }

  override def operationErrorMessage(msg: String): String = RestMessages.deleteSessionKeyError(msg)

  override def parseArgument(item: JValue) = item.extract[SessionKeyDeleteRequest]

}

class sessionKeyDeleteSlot extends sessionKeyDelete with DataOperationSlot[SessionKeyDeleteRequest]

class sessionKeyDeleteHttpSlot extends sessionKeyDelete with DataOperationHttpSlot[SessionKeyDeleteRequest]
