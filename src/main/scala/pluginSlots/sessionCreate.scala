package pluginSlots

import scala.util.Try
import io.simplifier.pluginapi.UserSession
import io.simplifier.session.{RestMessages, SessionManager}
import org.json4s._

case class SessionCreateResponse(success: Boolean, sessionid: String, message: String)

/**
 * Slot: Create Session.
 * @author Christian Simon
 */
trait sessionCreate extends DataOperation[NoArgument] {

  override def operation(arg: NoArgument)(implicit userSession: UserSession): Try[Any] = {
    log.debug(s"Create Session")
    SessionManager.createSession() map {
      session => SessionCreateResponse(true, session.sessionid, RestMessages.createSessionSuccess)
    }
  }

  override def operationErrorMessage(msg: String): String = RestMessages.createSessionError(msg)

  override def parseArgument(item: JValue) = NoArgument

}

class sessionCreateSlot extends sessionCreate with DataOperationSlot[NoArgument]

class sessionCreateHttpSlot extends sessionCreate with DataOperationHttpSlot[NoArgument]
