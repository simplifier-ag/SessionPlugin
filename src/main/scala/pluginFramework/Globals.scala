package pluginFramework

import io.simplifier.pluginapi.{PluginGlobalSettings, PluginGlobals}

/**
 * Global storage for the loaded settings.
 */
object GlobalSettings extends PluginGlobalSettings

/**
 * global plugin conf & init
 */
object Globals extends PluginGlobals(GlobalSettings) {

  override val registrationSecret: String = byDeployment.PluginRegistrationSecret()

}
