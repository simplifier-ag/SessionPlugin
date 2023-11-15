package io.simplifier.session

/** Exception leading to a REST error message. */
case class RestMessageException(msgText: String) extends Exception(msgText)

/**
 * Object containing all REST messages defined in the SessionPlugin.
 */
object RestMessages {

  /** Generator function for REST messages. */
  private def mkRestMessagePair(dataObject: String, op: String): (String, String => String) = {
    val successMessage = s"$dataObject has been $op successfully."
    val genErrorMessage: String => String = { reason =>
      s"$dataObject cannot be $op due to the following reason: $reason."
    }
    (successMessage, genErrorMessage)
  }

  val (createSessionSuccess, createSessionError) = mkRestMessagePair("Session", "created")
  val (fetchSessionSuccess, fetchSessionError) = mkRestMessagePair("Session", "fetched")
  val (overwriteSessionSuccess, overwriteSessionError) = mkRestMessagePair("Session", "overwritten")
  val (deleteSessionSuccess, deleteSessionError) = mkRestMessagePair("Session", "deleted")

  val (fetchSessionKeySuccess, fetchSessionKeyError) = mkRestMessagePair("Session key", "fetched")
  val (overwriteSessionKeySuccess, overwriteSessionKeyError) = mkRestMessagePair("Session key", "overwritten")
  val (deleteSessionKeySuccess, deleteSessionKeyError) = mkRestMessagePair("Session key", "deleted")

}
