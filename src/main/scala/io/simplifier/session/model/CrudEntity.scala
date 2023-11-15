package io.simplifier.session.model

import org.squeryl.Table
import org.squeryl.KeyedEntity
import org.squeryl.dsl.QueryDsl
import org.squeryl.PrimitiveTypeMode

/**
 * Trait providing CRUD operations on a keyed entity.
 * @author Christian Simon
 */
trait CrudEntity[T] {
  self: T with KeyedEntity[Int] =>

  implicit val dsl: QueryDsl = PrimitiveTypeMode

  def tableT: Table[T]

  def insert() = tableT.insert(this)

  def delete()(implicit ev: T <:< KeyedEntity[Int]) = tableT.delete(this.id)

  def update()(implicit ev: T <:< KeyedEntity[_]) = tableT.update(this)

}
