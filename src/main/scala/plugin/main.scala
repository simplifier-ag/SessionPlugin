package plugin

import io.simplifier.pluginapi.PluginApp
import io.simplifier.session.DbSession
import pluginFramework.{GlobalSettings, Globals}

/**
 * Main class for the plugin.
 */
object main extends PluginApp("session") {

  lazy val baseSlot = pluginSlot
  
  lazy val globalSettings = GlobalSettings
  
  lazy val globals = Globals

  /**
   * Init plugin.
   */
  override def init(): Unit = {
    log.info("Starting up Session plugin ...")
    DbSession.initializeMySQLFromConfig(Globals.settings)
  }

  /**
   * Shutdown plugin.
   */
  override def shutdown(): Unit = {
    log.info("Shutdown Session Plugin ...")
  }

}

