package models

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
                 var in_language: String,
                 var last_update: String,
                 var supp: String
                 ) extends KeyedEntity[Long]

object Theme {

  import Database.themesTable

  def allQ(codeLangue: String): Query[Theme] = from(themesTable) {
    theme => where(theme.language_id === codeLangue and theme.supp === "f") select theme orderBy("%02d".format(theme.number) asc)
  }

  def findAll(codeLangue: String): Iterable[Theme] = inTransaction {
    allQ(codeLangue).toList
  }

  def insert(theme: Theme): Theme = inTransaction {
    theme.last_update =
      DateTime.now(DateTimeZone.UTC).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"))
    themesTable.insert(theme)
  }

  def findByNumber(codeLangue: String, number: Int) = inTransaction {
    from(themesTable) ( t =>
      where(t.number === number and t.language_id === codeLangue and t.supp === "f")
        select t
    ).headOption
  }

  def findById(id: Long) = inTransaction {
    from(themesTable) ( t =>
      where(t.id === id and t.supp === "f")
        select t
    ).headOption
  }

  def remove(theme: Theme) = inTransaction {
    theme.last_update =
      DateTime.now(DateTimeZone.UTC).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"))
    theme.supp = "t"
    themesTable.update(theme)
  }

  def removeById(id: Long) = inTransaction {
    themesTable.update(t =>
        where(t.id === id)
    set(t.supp := "t",
        t.last_update  := DateTime.now(DateTimeZone.UTC).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")))
    )
  }

  def removeAll(codeLangue: String) = inTransaction {
    themesTable.update(t =>
      where(t.language_id === codeLangue)
        set(t.supp := "t",
        t.last_update  := DateTime.now(DateTimeZone.UTC).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")))
    )
  }

  def update(theme: Theme) = inTransaction {
    theme.last_update =
      DateTime.now(DateTimeZone.UTC).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"))
    themesTable.update(theme)
  }
  def lastNumber(codeLangue: String): Int = inTransaction {
    from(themesTable)(t =>
      where(t.language_id === codeLangue)
        compute nvl(max(t.number), 0)
    )
  }
  def maxUpdate(codeLangue: String): String = inTransaction {
    from(themesTable)(t =>
      where(t.language_id === codeLangue)
        compute nvl(max(t.last_update), "")
    )
  }
  def file_themes(codeLangue: String, fichier: List[String]): (Int,Int,Int) = {
    var nbUpdate = 0
    var nbInsert = 0
    var nbIgnored = 0
    for (ligne <- fichier) {
      val data = ligne.split(";")
      if (data.size == 2 && data(0).matches("^\\d+$")) {
        Theme.findByNumber(codeLangue, data(0).toInt) match {
          case Some(theme) =>
            theme.in_language = data(1)
            Theme.update(theme)
            nbUpdate += 1
          case None =>
            Theme.insert(Theme(0,codeLangue,data(0).toInt,data(1),"", "f"))
            nbInsert += 1
        }
      } else {
        nbIgnored += 1
      }
    }
    (nbUpdate, nbInsert, nbIgnored)
  }
}