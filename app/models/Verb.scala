package models


import models.Database._
import org.joda.time.format.DateTimeFormat
import org.joda.time.{DateTimeZone, DateTime}
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
                var last_update: String
                ) extends KeyedEntity[Long]

object Verb {

  import Database.verbsTable

  def allQ(codeLangue: String): Query[Verb] = from(verbsTable) {
    verb => where(verb.language_id === codeLangue) select(verb) orderBy(verb.in_language asc)
  }

  def findAll(codeLangue: String): Iterable[Verb] = inTransaction {
    allQ(codeLangue).toList
  }

  def insert(verb: Verb): Verb = inTransaction {
    verb.last_update =
      DateTime.now(DateTimeZone.UTC).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"))
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

  def removeById(id: Long) = inTransaction {
    verbsTable.delete(id)
  }

  def removeAll(codeLangue: String) = inTransaction {
    verbsTable.delete(allQ(codeLangue))
  }

  def update(verb: Verb) = inTransaction {
    verb.last_update =
      DateTime.now(DateTimeZone.UTC).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"))
    verbsTable.update(verb)
  }

  def maxUpdate(codeLangue: String): String = inTransaction {
    from(verbsTable)(v =>
      where(v.language_id === codeLangue)
        compute (nvl(max(v.last_update), ""))
    )
  }
}