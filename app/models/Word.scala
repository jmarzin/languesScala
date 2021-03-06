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
case class Word (
               id: Long,
               language_id: String,
               theme_id: Long,
               language_level: String,
               in_french: String,
               var sort_word: String,
               var in_language: String,
               var pronunciation: String,
               var last_update: String,
               var supp: String
               ) extends KeyedEntity[Long]

object Word {

  import Database.wordsTable

  def allQ(codeLangue: String): Query[Word] = from(wordsTable) {
    word => where(word.language_id === codeLangue and word.supp === "f") select word orderBy(word.sort_word, word.in_french asc)
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
      where(w.id === id and w.supp === "f")
        select w
    ).headOption
  }

  def findByThemeId(themeId: Long): Iterable[Word] = inTransaction {
    from(wordsTable) ( w =>
      where(w.theme_id === themeId and w.supp === "f")
        select w
        orderBy(w.sort_word, w.in_french asc)
    ).toList
  }

  def findByText(codeLangue: String, texte: String): Iterable[Word] = inTransaction {
    from(wordsTable) ( w =>
      where(w.language_id === codeLangue and w.supp === "f" and
          ((w.in_french like texte) or (w.in_language like texte) or (w.sort_word like texte)))
        select w
        orderBy(w.sort_word, w.in_french asc)
    ).toList
  }

  def findByThemeIdAndInFrench(theme_id: Long ,francais: String) = inTransaction {
    from(wordsTable) ( w =>
      where(w.theme_id === theme_id and w.supp === "f" and w.in_french === francais)
      select w
    ).headOption
  }

  def remove(word: Word) = inTransaction {
    word.last_update =
      DateTime.now(DateTimeZone.UTC).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"))
    word.supp = "t"
    wordsTable.update(word)
  }

  def removeById(id: Long) = inTransaction {
    wordsTable.update(w =>
      where(w.id === id)
        set(w.supp := "t",
        w.last_update  := DateTime.now(DateTimeZone.UTC).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")))
    )
  }

  def removeAll(codeLangue: String) = inTransaction {
    wordsTable.update(w =>
      where(w.language_id === codeLangue)
        set(w.supp := "t",
        w.last_update  := DateTime.now(DateTimeZone.UTC).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")))
    )
  }

  def update(word: Word) = inTransaction {
    word.last_update =
      DateTime.now(DateTimeZone.UTC).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"))
    wordsTable.update(word)
  }

  def maxUpdate(codeLangue: String): String = inTransaction {
    from(wordsTable)(w =>
      where(w.language_id === codeLangue)
        compute nvl(max(w.last_update), "")
    )
  }

  def file_words(codeLangue: String, fichier: List[String]): (Int,Int,Int) = {
    var nbUpdate = 0
    var nbInsert = 0
    var nbIgnored = 0
    val liste_themes = Theme.findAll(codeLangue).map (t =>
      (t.number, t.id)
    ).toMap
    for (ligne <- fichier) {
      val data = ligne.split(";")
      if (data.size >= 4 && data(0).matches("^\\d+$")) {
        liste_themes.get(data(0).toInt) match {
          case Some(theme_id) =>
            Word.findByThemeIdAndInFrench(theme_id, data(1)) match {
              case Some(word) =>
                word.in_language = data(3)
                word.sort_word = data(2)
                if(data.size == 4) {
                  word.pronunciation = data(4)
                }
                Word.update(word)
                nbUpdate += 1
              case None =>
                var pronunciation = ""
                if(data.size > 4) {
                  pronunciation = data(4)
                }
                Word.insert(Word(0,codeLangue,theme_id,"1",data(1),data(2),data(3),pronunciation,"", "f"))
                nbInsert += 1
            }
        }
      } else {
        nbIgnored += 1
      }
    }
    (nbUpdate,nbInsert,nbIgnored)
  }
}