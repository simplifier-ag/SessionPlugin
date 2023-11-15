package io.simplifier.session.model

import org.squeryl.Schema
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.ast.EqualityExpression

/**
 * DB Schemifier for Plugin Data Model.
 * @author Christian Simon
 */
object PluginSchemifier extends PluginSchema

/**
 * DB Schema for Plugin Data Model.
 * @author Christian Simon
 */
class PluginSchema extends Schema {

  /*
   * TABLES
   */

  val sessionT = table[Session]
  on(sessionT)(t => declare(
    t.sessionid is (unique, indexed("idxSessionId"))
  ))

}
