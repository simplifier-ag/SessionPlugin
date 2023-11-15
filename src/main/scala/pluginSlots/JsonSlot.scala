package pluginSlots

import io.simplifier.pluginapi.helper.PluginLogger
import io.simplifier.pluginapi.slots.{ChunkedHttpSlot, ChunkedSlot}
import io.simplifier.pluginapi.{HttpPostResponse, JSON, UserSession}
import io.simplifier.pluginbase.util.json.JSONFormatter.renderJSONCompact
import org.json4s._
import pluginFramework.Globals

trait JsonFormats {

  implicit val formats: Formats =  DefaultFormats.lossless

}

/**
 * Abstract HTTP slot for JSON processing.
 * @author Christian Simon
 */
trait HttpJsonSlot extends ChunkedHttpSlot with PluginLogger with JsonFormats {

  override def chunkHelper = Globals.chunkHelper

  val contentType = "application/json"

  override def slot(item: JValue)(implicit userSession: UserSession) = {
    HttpPostResponse(contentType, renderJSONCompact(result(item)).getBytes)
  }

  /**
   * Execute slot and create result.
   * @param item parameter as JSON value
   * @return result JSON value
   */
  def result(item: JValue)(implicit userSession: UserSession): JValue

}

/**
 * Abstract slot for JSON processing.
 * @author Christian Simon
 */
trait JsonSlot extends ChunkedSlot with PluginLogger with JsonFormats {

  override def chunkHelper = Globals.chunkHelper

  override def slot(item: JValue)(implicit userSession: UserSession) = {
    sender !! JSON(result(item), userSession)
  }

  /**
   * Execute slot and create result.
   * @param item parameter as JSON value
   * @return result JSON value
   */
  def result(item: JValue)(implicit userSession: UserSession): JValue

}
