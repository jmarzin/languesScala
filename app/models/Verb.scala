package models

import java.util.Date

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Query, KeyedEntity}

import scala.collection.Iterable

/**
 * Created by jacques on 23/02/15.
 */
case class Verb (
                id: Long,
                language_id: String,
                in_language: String,
                last_update: Date
                ) extends KeyedEntity[Long]

object Verb {

  import Database.verbsTable

  def allQ: Query[Verb] = from(verbsTable) {
    verb => select(verb) orderBy(verb.in_language asc)
  }
  def findAll: Iterable[Verb] = inTransaction {
    allQ.toList
  }
  def insert(verb: Verb): Verb = inTransaction {
    verbsTable.insert(verb)
  }
  def findById(id: Long) = inTransaction {
    from(verbsTable) ( t =>
      where(t.id === id)
        select(t)
    ).headOption
  }
  def remove(verb: Verb) = inTransaction {
    verbsTable.delete(verb.id)
  }
  def update(verb: Verb) { inTransaction {
    verbsTable.update(verb)
  }}
}