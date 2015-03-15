package models

/**
 * Created by jacques on 23/02/15.
 */
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Schema

object Database extends Schema {
  val themesTable = table[Theme]("themes")
  on(this.themesTable) { t => declare {
    t.id is(autoIncremented("themes__id_seq"))
  }}

  val wordsTable = table[Word]("words")
  on(this.wordsTable) { w => declare {
    w.id is(autoIncremented("words__id_seq"))
  }}

  val themeToWords = oneToManyRelation(themesTable, wordsTable).via((t,w) => t.id === w.theme_id)

  val verbsTable = table[Verb]("verbs")
  on(this.verbsTable) { v => declare {
    v.id is(autoIncremented("verbs__id_seq"))
  }}

  val formsTable = table[VForm]("forms")
  on(this.formsTable) { f => declare {
    f.id is(autoIncremented("forms__id_seq"))
  }}

//  val verbToForms = oneToManyRelation(verbsTable, formsTable).via((v,f) => v.id === f.verb_id)

  val formsTypeTable = table[FormType]("formstypes")
  on(this.formsTypeTable) { ft => declare {
    ft.id is(autoIncremented("formstypes__id_seq"))
  }}
  val formTypeToForms = oneToManyRelation(formsTypeTable, formsTable).via((ft,f) => ft.id === f.form_type_id)

}