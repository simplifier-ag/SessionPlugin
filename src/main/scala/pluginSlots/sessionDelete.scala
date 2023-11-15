package pluginSlots

import io.simplifier.pluginapi.UserSession
import io.simplifier.session.{RestMessages, SessionManager}
import org.json4s._

import scala.util.Try

case class SessionDeleteRequest(sessionid: String)

case class SessionDeleteResponse(success: Boolean, message: String)

/**
 * Slot: Delete Session.
 * @author Christian Simon
 */
trait sessionDelete extends DataOperation[SessionDeleteRequest] {

  override def operation(arg: SessionDeleteRequest)(implicit userSession: UserSession): Try[Any] = {
    log.debug(s"Delete Session ${arg.sessionid}")
    SessionManager.deleteSession(arg.sessionid) map {
      session => SessionDeleteResponse(true, RestMessages.deleteSessionSuccess)
    }
  }

  override def operationErrorMessage(msg: String): String = RestMessages.deleteSessionError(msg)

  override def parseArgument(item: JValue) = item.extract[SessionDeleteRequest]

}

class sessionDeleteSlot extends sessionDelete with DataOperationSlot[SessionDeleteRequest]

class sessionDeleteHttpSlot extends sessionDelete with DataOperationHttpSlot[SessionDeleteRequest]
