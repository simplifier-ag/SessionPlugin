package pluginAssets

import io.simplifier.pluginapi.{ChunkHelper, HttpPostResponse}
import io.simplifier.pluginapi.helper.PluginLogger
import io.simplifier.pluginapi.slots.ChunkedAssetHandler
import org.apache.commons.io.{FilenameUtils, IOUtils}
import pluginFramework.Globals

/**
 * Asset Handler for Session Plugin.
 * @author Christian Simon
 */
class AssetHandler extends ChunkedAssetHandler with PluginLogger {

  override def chunkHelper: ChunkHelper = Globals.chunkHelper

  def fromResource(path: String, mime: String): Option[HttpPostResponse] = {
    val maybeUrl = Option(getClass.getClassLoader.getResource(path))
    maybeUrl map {
      url =>
        HttpPostResponse(mime, IOUtils.toByteArray(url))
    }
  }

  def mimeByExt(path: String): String = FilenameUtils.getExtension(path).toLowerCase match {
    case "html"       => "text/html"
    case _ => {
      log.warn("Unknown extension for " + path)
      "application/octet-stream"
    }
  }

  private val ASSETS = "([a-zA-Z0-9._-]+)".r

  def handleAsset(path: String): Option[HttpPostResponse] = path match {
    case ASSETS(path) => fromResource("assets/" + path, mimeByExt(path))
    case other => {
      log.info(s"Asset Not Found: $path")
      None
    }
  }

}

