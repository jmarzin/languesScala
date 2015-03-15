package models

import java.util.Date

import org.joda.time.format.DateTimeFormat
import org.joda.time.{DateTimeZone, DateTime}
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Query, KeyedEntity}

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
               var last_update: String
               ) extends KeyedEntity[Long]

object VForm {

  import Database.formsTable
  import Database.formsTypeTable

  def allQ(codeLangue: String): Query[VForm] = from(formsTable) {
    vForm => where(vForm.language_id === codeLangue) select(vForm)
  }

  def allQByVerbId(verbId: Long): Query[VForm] = from(formsTable) {
    vForm => where(vForm.verb_id === verbId) select(vForm)
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
      where(f.verb_id === verbe_id and f.form_type_id === ft.id)
      select(Tuple2(f,ft))
      orderBy(ft.number asc)
    ).toList
  }

  def findById(id: Long) = inTransaction {
    from(formsTable) ( f =>
      where(f.id === id)
        select(f)
    ).headOption
  }

  def remove(vForm: VForm) = inTransaction {
    formsTable.delete(vForm.id)
  }

  def removeById(id: Long) = inTransaction {
    formsTable.delete(id)
  }

  def removeAll(codeLangue: String) = inTransaction {
    formsTable.delete(allQ(codeLangue))
  }

  def removeByVerb(verbId: Long) = inTransaction {
    formsTable.delete(allQByVerbId(verbId))
  }

  def update(vForm: VForm) = inTransaction {
    vForm.last_update =
      DateTime.now(DateTimeZone.UTC).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"))
    formsTable.update(vForm)
  }

  def maxUpdate(codeLangue: String): String = inTransaction {
    from(formsTable)(f =>
      where(f.language_id === codeLangue)
        compute (nvl(max(f.last_update), ""))
    )
  }
}