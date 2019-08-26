package models

import models.Database.themesTable
import org.joda.time.format.DateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{KeyedEntity, Query}

import scala.collection.Iterable

/**
 * Created by jacques on 23/02/15.
 */

case class Verb (
                id: Long,
                language_id: String,
                in_language: String,
                var last_update: String,
                var supp: String
                ) extends KeyedEntity[Long]

object Verb {

  import Database.verbsTable

  def allQ(codeLangue: String): Query[Verb] = from(verbsTable) {
    verb => where(verb.language_id === codeLangue and verb.supp === "f") select verb orderBy(verb.in_language asc)
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
      where(t.id === id and t.supp === "f")
        select t
    ).headOption
  }

  def remove(verb: Verb) = inTransaction {
    verb.last_update =
      DateTime.now(DateTimeZone.UTC).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"))
    verb.supp = "t"
    verbsTable.update(verb)
  }

  def removeById(id: Long) = inTransaction {
    verbsTable.update(v =>
      where(v.id === id)
        set(v.supp := "t",
        v.last_update  := DateTime.now(DateTimeZone.UTC).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")))
    )
  }

  def removeAll(codeLangue: String) = inTransaction {
    verbsTable.update(v =>
      where(v.language_id === codeLangue)
        set(v.supp := "t",
        v.last_update  := DateTime.now(DateTimeZone.UTC).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")))
    )
  }

  def update(verb: Verb) = inTransaction {
    verb.last_update =
      DateTime.now(DateTimeZone.UTC).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"))
    verbsTable.update(verb)
  }

  def maxUpdate(codeLangue: String): String = inTransaction {
    from(verbsTable)(v =>
      where(v.language_id === codeLangue)
        compute nvl(max(v.last_update), "")
    )
  }
}