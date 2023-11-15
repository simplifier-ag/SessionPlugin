package io.simplifier.session.model

import org.squeryl.KeyedEntity
import org.squeryl.annotations.Column
import org.joda.time.DateTime
import java.sql.Timestamp

/**
 * Model: Session.
 * @author Christian Simon
 */
class Session(
    @Column("id") var id: Int,
    var sessionid: String,
    @Column("user") var userId: Long,
    @Column("expires") var expires: Timestamp,
    @Column("data") var data: Array[Byte]) extends KeyedEntity[Int] with CrudEntity[Session] {

  override def tableT = PluginSchemifier.sessionT

  def isExpired = new DateTime(expires).isBeforeNow()

}