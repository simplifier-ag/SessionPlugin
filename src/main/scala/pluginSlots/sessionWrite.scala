package pluginSlots

import io.simplifier.pluginapi.UserSession
import io.simplifier.session.{RestMessages, SessionManager}
import org.json4s._

import scala.util.Try

case class SessionWriteRequest(sessionid: String, sessionData: JObject)

case class SessionWriteResponse(success: Boolean, message: String)

/**
 * Slot: Write Session Data.
 * @author Christian Simon
 */
trait sessionWrite extends DataOperation[SessionWriteRequest] {

  override def operation(arg: SessionWriteRequest)(implicit userSession: UserSession): Try[Any] = {
    log.debug(s"Write data for Session ${arg.sessionid}")
    SessionManager.overwriteSessionData(arg.sessionid, arg.sessionData) map {
      session => SessionWriteResponse(true, RestMessages.overwriteSessionSuccess)
    }
  }

  override def operationErrorMessage(msg: String): String = RestMessages.overwriteSessionError(msg)

  override def parseArgument(item: JValue) = item.extract[SessionWriteRequest]

}

class sessionWriteSlot extends sessionWrite with DataOperationSlot[SessionWriteRequest]

class sessionWriteHttpSlot extends sessionWrite with DataOperationHttpSlot[SessionWriteRequest]
