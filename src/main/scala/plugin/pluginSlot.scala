package plugin

import akka.actor.Props
import io.simplifier.pluginapi.BaseProps
import pluginAssets.AssetHandler
import pluginSlots._

object pluginSlot extends BaseProps {

  val slots = Map(
    "sessionCreate" -> Props[sessionCreateSlot],
    "sessionWrite" -> Props[sessionWriteSlot],
    "sessionFetch" -> Props[sessionFetchSlot],
    "sessionDelete" -> Props[sessionDeleteSlot],
    "sessionKeyWrite" -> Props[sessionKeyWriteSlot],
    "sessionKeyFetch" -> Props[sessionKeyFetchSlot],
    "sessionKeyDelete" -> Props[sessionKeyDeleteSlot])

  val httpSlots = Map(
    "sessionCreateHttp" -> Props[sessionCreateHttpSlot],
    "sessionWriteHttp" -> Props[sessionWriteHttpSlot],
    "sessionFetchHttp" -> Props[sessionFetchHttpSlot],
    "sessionDeleteHttp" -> Props[sessionDeleteHttpSlot],
    "sessionKeyWriteHttp" -> Props[sessionKeyWriteHttpSlot],
    "sessionKeyFetchHttp" -> Props[sessionKeyFetchHttpSlot],
    "sessionKeyDeleteHttp" -> Props[sessionKeyDeleteHttpSlot])

  override val assets = Props[AssetHandler]

}

