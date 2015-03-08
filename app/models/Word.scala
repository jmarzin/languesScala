package models

import java.util.Date

import models.Database._
import org.joda.time.format.DateTimeFormat
import org.joda.time.{DateTimeZone, DateTime}
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Query, KeyedEntity}

import scala.collection.Iterable

/**
 * Created by jacques on 23/02/15.
 */
case class Word (
               id: Long,
               language_id: String,
               theme_id: Long,
               in_french: String,
               sort_word: String,
               in_language: String,
               var last_update: String
               ) extends KeyedEntity[Long]

object Word {

  import Database.wordsTable

  def allQ(codeLangue: String): Query[Word] = from(wordsTable) {
    word => where(word.language_id === codeLangue) select(word) orderBy(word.sort_word+word.in_french asc)
  }
  def findAll(codeLangue: String): Iterable[Word] = inTransaction {
    allQ(codeLangue).toList
  }
  def insert(word: Word): Word = inTransaction {
    word.last_update =
      DateTime.now(DateTimeZone.UTC).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"))
    wordsTable.insert(word)
  }

  def findById(id: Long) = inTransaction {
    from(wordsTable) ( w =>
      where(w.id === id)
        select(w)
    ).headOption
  }

  def findByThemeId(themeId: Long): Iterable[Word] = inTransaction {
    from(wordsTable) ( w =>
      where(w.theme_id === themeId)
        select(w)
        orderBy(w.sort_word+w.in_french asc)
    ).toList
  }

  def findByText(codeLangue: String, texte: String): Iterable[Word] = inTransaction {
    from(wordsTable) ( w =>
      where((w.in_french like texte) or (w.in_language like texte) or (w.sort_word like texte))
        select(w)
        orderBy(w.sort_word+w.in_french asc)
    ).toList
  }

  def remove(word: Word) = inTransaction {
    wordsTable.delete(word.id)
  }

  def removeById(id: Long) = inTransaction {
    wordsTable.delete(id)
  }

  def removeAll(codeLangue: String) = inTransaction {
    wordsTable.delete(allQ(codeLangue))
  }

  def update(word: Word) = inTransaction {
    word.last_update =
      DateTime.now(DateTimeZone.UTC).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"))
    wordsTable.update(word)
  }

  def maxUpdate(codeLangue: String): String = inTransaction {
    from(wordsTable)(w =>
      where(w.language_id === codeLangue)
        compute (nvl(max(w.last_update), ""))
    )
  }
}