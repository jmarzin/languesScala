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
case class VForm(
               id: Long,
               language_id: String,
               verb_id: Long,
               form_type_id: Long,
               in_language: String,
               var last_update: String,
               var supp: String
               ) extends KeyedEntity[Long]

object VForm {

  import Database.formsTable
  import Database.formsTypeTable

  def allQ(codeLangue: String): Query[VForm] = from(formsTable) {
    vForm => where(vForm.language_id === codeLangue and vForm.supp === "f") select vForm
  }

  def allQByVerbId(verbId: Long): Query[VForm] = from(formsTable) {
    vForm => where(vForm.verb_id === verbId and vForm.supp === "f") select vForm
  }

  def findAll(codeLangue: String): Iterable[VForm] = inTransaction {
    allQ(codeLangue).toList
  }

  def insert(vForm: VForm): VForm = inTransaction {
    vForm.last_update =
      DateTime.now(DateTimeZone.UTC).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"))
    formsTable.insert(vForm)
  }

  def findByVerb(verbe_id: Long): List[(VForm,FormType)] = inTransaction {
    from(formsTable, formsTypeTable)((f, ft) =>
      where(f.verb_id === verbe_id and f.form_type_id === ft.id and f.supp === "f")
      select Tuple2(f, ft)
      orderBy(ft.number asc)
    ).toList
  }

  def findById(id: Long) = inTransaction {
    from(formsTable) ( f =>
      where(f.id === id and f.supp === "f")
        select f
    ).headOption
  }

  def remove(vForm: VForm) = inTransaction {
    vForm.last_update =
      DateTime.now(DateTimeZone.UTC).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"))
    vForm.supp = "t"
    formsTable.update(vForm)
  }

  def removeById(id: Long) = inTransaction {
    formsTable.update(f =>
      where(f.id === id)
        set(f.supp := "t",
        f.last_update  := DateTime.now(DateTimeZone.UTC).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")))
    )
  }

  def removeAll(codeLangue: String) = inTransaction {
    formsTable.update(f =>
      where(f.language_id === codeLangue)
        set(f.supp := "t",
        f.last_update  := DateTime.now(DateTimeZone.UTC).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")))
    )
  }

  def removeByVerb(verbId: Long) = inTransaction {
    formsTable.update(f =>
      where(f.verb_id === verbId)
      set(f.supp := "t",
      f.last_update  := DateTime.now(DateTimeZone.UTC).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"))))
  }

  def update(vForm: VForm) = inTransaction {
    vForm.last_update =
      DateTime.now(DateTimeZone.UTC).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"))
    formsTable.update(vForm)
  }

  def maxUpdate(codeLangue: String): String = inTransaction {
    from(formsTable)(f =>
      where(f.language_id === codeLangue)
        compute nvl(max(f.last_update), "")
    )
  }
}