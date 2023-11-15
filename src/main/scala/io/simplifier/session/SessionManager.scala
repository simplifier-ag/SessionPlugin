package io.simplifier.session

import io.simplifier.session.model.PluginSchemifier.sessionT
import io.simplifier.pluginapi.UserSession
import io.simplifier.pluginapi.helper.PluginLogger
import io.simplifier.pluginbase.util.json.JSONCompatibility._
import io.simplifier.session.util.json.JSONCompatibility.LegacySearch
import io.simplifier.pluginbase.util.json.JSONFormatter.renderJSONCompact
import io.simplifier.session.model.Session
import io.simplifier.session.util.json.JSONCompatibility
import org.joda.time.DateTime.now
import org.json4s._
import org.squeryl.PrimitiveTypeMode._
import pluginFramework.Globals

import java.sql.Timestamp
import java.util.UUID.randomUUID
import scala.util.{Failure, Success, Try}

/**
 * Session manager.
 * @author Christian Simon
 */
object SessionManager extends PluginLogger {

  private val PermissionDeniedUnauthenticated = "Permission Denied: Unauthenticated"
  private val PermissionDeniedUserNotMatching = "Permission Denied: User not matching session"
  private val SessionNotFoundOrExpired = "Session not found or expired"
  private val SessionDataInvalid = "Session data contains invalid JSON"
  private val SessionKeyMissing = "Session key must not be empty"

  // Number of minutes after which a session is expired and can no longer be used
  private val expirationMinutes = Globals.settings.getInt("sessionPlugin.expireTimeout")

  /**
   * Require non-empty user and run block parameter with given userId
   */
  private def requireUser[A](userSession: UserSession)(block: Long => Try[A]): Try[A] = userSession.userIdOpt match {
    case None         => Failure(RestMessageException(PermissionDeniedUnauthenticated))
    case Some(userId) => block(userId)
  }

  /**
   * Require existing, non-expired session for the userId and run block with given session.
   */
  private def requireValidSession[A](sessionid: String, userId: Long)(block: Session => Try[A]): Try[A] = {
    Try(sessionT.where { _.sessionid === sessionid }.headOption) flatMap {
      case None => Failure(RestMessageException(SessionNotFoundOrExpired))
      case Some(session) if session.isExpired => {
        log.info(s"Expire session ${session.sessionid} ...")
        session.delete()
        Failure(RestMessageException(SessionNotFoundOrExpired))
      }
      case Some(session) => Success(session)
    } flatMap {
      session =>
        if (session.userId != userId)
          Failure(RestMessageException(PermissionDeniedUserNotMatching))
        else
          block(session)
    }
  }

  /**
   * Check if parameter is not emtpy and throw message exception otherwise.
   */
  def checkNotEmpty(value: String, errorMessage: => String): Unit =
    if (value.trim == "") throw new RestMessageException(errorMessage)

  /**
   * Calculate new expireation date from now on
   */
  private def createExpirationDate(): Timestamp = new Timestamp(now().plusMinutes(expirationMinutes).toDate().getTime)

  /**
   * Create Unique ID
   */
  private def createUUID() = randomUUID().toString

  /**
   * Serialize Session data from JSON to ByteArray.
   */
  private def serializeData(data: JObject): Array[Byte] = renderJSONCompact(data).getBytes("UTF-8")

  /**
   * Unserialize Session data from ByteArray to JSON.
   */
  private def unserializeData(bytes: Array[Byte]): JObject = parseJsonOrEmptyString(new String(bytes)) match {
    case obj: JObject => obj
    case other        => throw RestMessageException(SessionDataInvalid)
  }

  /**
   * Operation: Create new session (assigned to given userId).
   */
  def createSession()(implicit userSession: UserSession): Try[Session] = requireUser(userSession) {
    userId =>
      val sessionid = createUUID()
      val data = JObject(List())
      val expiration = createExpirationDate()
      Try {
        val session = new Session(0, sessionid, userId, expiration, serializeData(data))
        transaction {
          session.insert()
        }
      }
  }

  /**
   * Operation: Delete session with given sessionId.
   */
  def deleteSession(sessionid: String)(implicit userSession: UserSession): Try[Session] = requireUser(userSession) {
    userId =>
      transaction {
        requireValidSession(sessionid, userId) {
          session =>
            Try {
              session.delete()
              session
            }
        }
      }
  }

  /**
   * Operation: Overwrite session with given sessionId with the given JSON data.
   */
  def overwriteSessionData(sessionid: String, data: JObject)(implicit userSession: UserSession): Try[Session] = requireUser(userSession) {
    userId =>
      transaction {
        requireValidSession(sessionid, userId) {
          session =>
            Try {
              session.data = serializeData(data)
              session.expires = createExpirationDate()
              session.update()
              session
            }
        }
      }
  }

  /**
   * Operation: Fetch session data from session.
   */
  def getSessionData(sessionid: String)(implicit userSession: UserSession): Try[JObject] = requireUser(userSession) {
    userId =>
      transaction {
        requireValidSession(sessionid, userId) {
          session =>
            Try {
              session.expires = createExpirationDate()
              session.update()
              unserializeData(session.data)
            }
        }
      }
  }

  /**
   * Operation: Write/Overwrite only one key in the session data of a session.
   */
  def overwriteSessionKey(sessionid: String, key: String, data: JValue)(implicit userSession: UserSession): Try[Session] = requireUser(userSession) {
    userId =>
      checkNotEmpty(key, SessionKeyMissing)
      transaction {
        requireValidSession(sessionid, userId) {
          session =>
            Try {
              val sessionData = unserializeData(session.data)
              val existing = LegacySearch(sessionData) \ key != JNothing
              val updatedSessionData: JObject = if (existing)
                sessionData.replace(key :: Nil, data).asInstanceOf[JObject]
              else
               JObject(sessionData.obj :+  JField(key, data))
              session.data = serializeData(updatedSessionData)
              session.expires = createExpirationDate()
              session.update()
              session
            }
        }
      }
  }

  /**
   * Operation: Delete only a key in the session data of a session. If the key is already empty, nothing happens.
   */
  def deleteSessionKey(sessionid: String, key: String)(implicit userSession: UserSession): Try[Session] = requireUser(userSession) {
    userId =>
      checkNotEmpty(key, SessionKeyMissing)
      transaction {
        requireValidSession(sessionid, userId) {
          session =>
            Try {
              val sessionData = unserializeData(session.data)
              val updatedSessionData = sessionData.replace(key :: Nil, JNothing).asInstanceOf[JObject]
              session.data = serializeData(updatedSessionData)
              session.expires = createExpirationDate()
              session.update()
              session
            }
        }
      }
  }

  /**
   * Operation: Get the data associated with a key in the session. If the key does not exist, JNothing will be returned.
   */
  def getSessionKey(sessionid: String, key: String)(implicit userSession: UserSession): Try[JValue] = requireUser(userSession) {
    userId =>
      checkNotEmpty(key, SessionKeyMissing)
      transaction {
        requireValidSession(sessionid, userId) {
          session =>
            Try {
              session.expires = createExpirationDate()
              session.update()
              val sessionData = unserializeData(session.data)
              LegacySearch(sessionData) \ key
            }
        }
      }
  }

}