package models

import java.sql.Timestamp
import java.util.Date
import models.Database._
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
case class FormType (
                    id: Long,
                    language_id: String,
                    number: Int,
                    in_french: String,
                    var last_update: String
                    ) extends KeyedEntity[Long]

object FormType {

  import Database.formsTypeTable

  def allQ(codeLangue: String): Query[FormType] = from(formsTypeTable) {
    formType => where(formType.language_id === codeLangue) select(formType) orderBy("%02d".format(formType.number) asc)
  }

  def findAll(codeLangue: String): Iterable[FormType] = inTransaction {
    allQ(codeLangue).toList
  }

  def insert(formType: FormType): FormType = inTransaction {
    formType.last_update =
      DateTime.now(DateTimeZone.UTC).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"))
    formsTypeTable.insert(formType)
  }

  def findByNumber(number: Int) = inTransaction {
    from(formsTypeTable) ( ft =>
      where(ft.number === number)
        select(ft)
    ).headOption
  }

  def findById(id: Long) = inTransaction {
    from(formsTypeTable) ( ft =>
      where(ft.id === id)
        select(ft)
    ).headOption
  }

  def remove(formType: FormType) = inTransaction {
    formsTypeTable.delete(formType.id)
  }

  def removeById(id: Long) = inTransaction {
    formsTypeTable.delete(id)
  }

  def update(formType: FormType) = inTransaction {
    formType.last_update =
      DateTime.now(DateTimeZone.UTC).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"))
    formsTypeTable.update(formType)
  }
  def lastNumber(codeLangue: String): Int = inTransaction {
    from(formsTypeTable)(ft =>
      where(ft.language_id === codeLangue)
        compute (nvl(max(ft.number), 0))
    )
  }
  def maxUpdate(codeLangue: String): String = inTransaction {
    from(formsTypeTable)(ft =>
      where(ft.language_id === codeLangue)
        compute (nvl(max(ft.last_update), ""))
    )
  }
}