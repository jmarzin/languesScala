package models

import java.sql.Timestamp
import java.util.Date
import org.joda.time.format.DateTimeFormat
import org.joda.time.{DateTimeZone, DateTime}
import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Query
import collection.Iterable
import scala.language.postfixOps

/**
 * Created by jacques on 23/02/15.
 */
case class Theme (
                 id: Long,
                 language_id: String,
                 number: Int,
                 in_language: String,
                 var last_update: String
                 ) extends KeyedEntity[Long]

object Theme {

  import Database.themesTable

  def allQ(codeLangue: String): Query[Theme] = from(themesTable) {
    theme => where(theme.language_id === codeLangue) select(theme) orderBy("%02d".format(theme.number) asc)
  }

  def findAll(codeLangue: String): Iterable[Theme] = inTransaction {
    allQ(codeLangue).toList
  }

  def insert(theme: Theme): Theme = inTransaction {
    theme.last_update =
      DateTime.now(DateTimeZone.UTC).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"))
    themesTable.insert(theme)
  }

  def findByNumber(number: Int) = inTransaction {
    from(themesTable) ( t =>
      where(t.number === number)
        select(t)
    ).headOption
  }

  def findById(id: Long) = inTransaction {
    from(themesTable) ( t =>
      where(t.id === id)
        select(t)
    ).headOption
  }

  def remove(theme: Theme) = inTransaction {
    themesTable.delete(theme.id)
  }

  def removeById(id: Long) = inTransaction {
    themesTable.delete(id)
  }

  def removeAll(codeLangue: String) = inTransaction {
    themesTable.delete(allQ(codeLangue))
  }

  def update(theme: Theme) = inTransaction {
    theme.last_update =
      DateTime.now(DateTimeZone.UTC).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"))
    themesTable.update(theme)
  }
  def lastNumber(codeLangue: String): Int = inTransaction {
    from(themesTable)(t =>
      where(t.language_id === codeLangue)
        compute (nvl(max(t.number), 0))
    )
  }
  def maxUpdate(codeLangue: String): String = inTransaction {
    from(themesTable)(t =>
      where(t.language_id === codeLangue)
        compute (nvl(max(t.last_update), ""))
    )
  }
}