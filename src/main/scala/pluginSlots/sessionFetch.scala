package pluginSlots

import io.simplifier.pluginapi.UserSession
import io.simplifier.session.{RestMessages, SessionManager}
import org.json4s._

import scala.util.Try

case class SessionFetchRequest(sessionid: String)

case class SessionFetchResponse(success: Boolean, result: JObject, message: String)

/**
 * Slot: Fetch Session Data.
 * @author Christian Simon
 */
trait sessionFetch extends DataOperation[SessionFetchRequest] {

  override def operation(arg: SessionFetchRequest)(implicit userSession: UserSession): Try[Any] = {
    log.debug(s"Fetch Session ${arg.sessionid}")
    SessionManager.getSessionData(arg.sessionid) map {
      sessionData => SessionFetchResponse(true, sessionData, RestMessages.fetchSessionSuccess)
    }
  }

  override def operationErrorMessage(msg: String): String = RestMessages.fetchSessionError(msg)

  override def parseArgument(item: JValue) = item.extract[SessionFetchRequest]

}

class sessionFetchSlot extends sessionFetch with DataOperationSlot[SessionFetchRequest]

class sessionFetchHttpSlot extends sessionFetch with DataOperationHttpSlot[SessionFetchRequest]
