package io.simplifier.session

import com.typesafe.config.Config
import io.simplifier.pluginapi.helper.PluginLogger
import io.simplifier.session.util.db.SquerylInit

import scala.collection.JavaConverters._

/**
 * Database Session Management.
 * @author Christian Simon
 */
object DbSession extends PluginLogger {

  /**
    * Initialize MySQL Session from Application Config.
    */
  def initializeMySQLFromConfig(config: Config) = {
    val mysql = config.getObject("database_mysql").unwrapped.asScala
    val host = mysql("host").toString
    val port = mysql("port").toString
    val database = mysql("database").toString
    val user = mysql("user").toString
    val pass = mysql("pass").toString

    val poolSize = mysql.get("pool-size").map(_.toString.toInt).getOrElse (20)

    SquerylInit.initWith (SquerylInit.DbConfig (host, port, database, SquerylInit.MySql, user, pass, poolSize, None))
  }


  /**
   * Initialize H2 Session from fixed settings (for testing).
   */
  def initializeH2(path: String) = SquerylInit.initWith(SquerylInit.H2FileConfig(path))
}