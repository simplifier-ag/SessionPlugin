package pluginSlots

import io.simplifier.pluginapi.UserSession
import io.simplifier.session.{RestMessages, SessionManager}
import org.json4s._

import scala.util.Try

case class SessionKeyWriteRequest(sessionid: String, key: String, sessionData: JValue)

case class SessionKeyWriteResponse(success: Boolean, message: String)

/**
 * Slot: Write/Overwrite Session Key.
 * @author Christian Simon
 */
trait sessionKeyWrite extends DataOperation[SessionKeyWriteRequest] {

  override def operation(arg: SessionKeyWriteRequest)(implicit userSession: UserSession): Try[Any] = {
    log.debug(s"Write data for Session ${arg.sessionid} / '${arg.key}'")
    SessionManager.overwriteSessionKey(arg.sessionid, arg.key, arg.sessionData) map {
      session => SessionKeyWriteResponse(true, RestMessages.overwriteSessionKeySuccess)
    }
  }

  override def operationErrorMessage(msg: String): String = RestMessages.overwriteSessionKeyError(msg)

  override def parseArgument(item: JValue) = item.extract[SessionKeyWriteRequest]

}

class sessionKeyWriteSlot extends sessionKeyWrite with DataOperationSlot[SessionKeyWriteRequest]

class sessionKeyWriteHttpSlot extends sessionKeyWrite with DataOperationHttpSlot[SessionKeyWriteRequest]
