package plugin

import com.typesafe.config.ConfigFactory
import io.simplifier.pluginapi.helper.PluginLogger
import io.simplifier.session.DbSession
import io.simplifier.session.model.PluginSchemifier
import org.squeryl.PrimitiveTypeMode._

import java.io.{ByteArrayOutputStream, PrintWriter}

/**
 * Admin tasks.
 * @author Christian Simon
 */
object admin extends App with PluginLogger {

  def prepareSqlSession(): Unit = {
    val settings = ConfigFactory.load("settings")
    DbSession.initializeMySQLFromConfig(settings)
  }

  def schemify(): Unit = {
    log.info("Running schemify ...")
    prepareSqlSession()
    transaction {
      val ddl = new ByteArrayOutputStream
      val print = new PrintWriter(ddl)
      PluginSchemifier.printDdl(print)
      print.flush()
      log.debug(s"Create table Statements:\n${ddl.toString}")

      PluginSchemifier.drop
      PluginSchemifier.create
    }
  }

  def help(): Unit = {
    log.info("""|Admin Commands:
                |  admin help       : Print this help
                |  admin schemify   : Create DB Schema
                |""".stripMargin)
  }

  // This sets the logfile basename, so it must be called before the first Logger is initialized!
  PluginLogger.initializeClusterModeLoggingProperties("session")

  args.toList match {
    case "schemify" :: Nil   => admin.schemify()
    case "help" :: Nil       => admin.help()
    case _                 => log.error("Unknown command - Try 'admin help'")
  }

}