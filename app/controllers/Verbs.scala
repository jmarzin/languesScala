package controllers

import models.{Verb, VForm, FormType}
import play.api.data.{Forms, Form}
import play.api.data.Forms.{mapping, tuple, longNumber, number, text, nonEmptyText}
import play.api.i18n.Messages
import play.api.libs.json.{Json, JsString, JsNumber}
import play.api.mvc.{Flash, Action, Controller}
import org.squeryl.PrimitiveTypeMode._

/**
 * Created by jacques on 25/02/15.
 */
object Verbs extends Controller{

  val verbMapping = mapping(
    "id" -> longNumber,
    "language_id" -> nonEmptyText,
    "in_language" -> nonEmptyText,
    "last_update" -> text
  )(Verb.apply)(Verb.unapply)

  val vFormMapping = mapping(
    "id" -> longNumber,
    "language_id" -> nonEmptyText,
    "verb_id" -> longNumber,
    "form_type_id" -> longNumber,
    "in_language" -> text,
    "last_update" -> text
  )(VForm.apply)(VForm.unapply)

  val formTypeMapping = mapping(
    "id" -> longNumber,
    "language_id" -> nonEmptyText,
    "number" -> number(min = 1),
    "in_language" -> nonEmptyText,
    "last_update" ->text
  )(FormType.apply)(FormType.unapply)

  val VFormCompleteMapping = tuple(
    "forme" -> vFormMapping,
    "type" -> formTypeMapping
  )

  private def verbForm = Form(
    tuple(
      "verbe" -> verbMapping,
      "formes" -> Forms.list(VFormCompleteMapping)
  ))

  def list(codeLangue: String, page: Int) = Action { implicit request =>
    if (request.session.isEmpty)
      Redirect(routes.Themes.changeLanguage("italien"))
    else {
      val liste = Verb.findAll(codeLangue)
      var pageCourante = page
      val maxpage:Int = ((liste.size - 1) /15) + 1
      if (pageCourante > maxpage) {
        pageCourante = maxpage
      } else if (page < 1) {
        pageCourante = 1
      }
      val indicPrec = pageCourante > 1
      val indicSuiv = pageCourante < maxpage
      Ok(views.html.verbs.list(
        liste.dropRight(math.max(0, liste.size - pageCourante * 15)).drop((pageCourante - 1) * 15),
        indicPrec, indicSuiv, pageCourante))
    }
  }

  def newVerb(codeLangue: String) = Action { implicit request =>
    val form = if (request.flash.get("error").isDefined) {
      val errorForm = this.verbForm.bind(request.flash.data)
      errorForm
    } else {
      val newVerb = Verb(0, codeLangue, "", "")
      val newVForm = VForm(0, codeLangue, 0, 0, "", "")
      val listeTypes = FormType.findAll(codeLangue)
      val verb = (newVerb,listeTypes.map(t => (VForm(0,codeLangue,0,t.id,"",""),t)).toList)
      this.verbForm.fill(verb)
    }
    Ok(views.html.verbs.editVerb(form))
  }

  def save(codeLangue: String) = Action { implicit request =>
    val newVerbForm = this.verbForm.bindFromRequest()
    newVerbForm.fold(
      hasErrors = { form =>
        Redirect(routes.Verbs.newVerb(request.session.get("codeLangue").get)).flashing(Flash(form.data) +
          ("error" -> Messages("validation.errors")))
      },
      success = { newVerb =>
        transaction {
          Verb.insert(newVerb._1)
          newVerb._2.foreach(n =>
            VForm.insert(VForm(0,
              n._1.language_id,
              newVerb._1.id,
              n._1.form_type_id,
              n._1.in_language,
              ""
            ))
          )
        }
        val successMessage = "success" -> Messages("verbe.new.success", newVerb._1.id)
        Redirect(routes.Verbs.show(newVerb._1.id)).flashing(successMessage)
      }
    )
  }

  def show(id: Long) = Action { implicit request =>
    Verb.findById(id).map { verb =>
      Ok(views.html.verbs.details(verb))
    }.getOrElse(NotFound)
  }

  def edit(id: Long) = Action { implicit request =>
    Verb.findById(id).map { verb =>
      val formes = VForm.findByVerb(verb.id)
      Ok(views.html.verbs.editVerb(this.verbForm.fill((verb,formes)), Option(verb.id)))
    }.getOrElse(NotFound)
  }

  def update(id: Long) = Action { implicit request =>
    val newVerbForm = this.verbForm.bindFromRequest()
    newVerbForm.fold(
      hasErrors = { form =>
        Ok(views.html.verbs.editVerb(form, Option(id))).flashing(Flash(form.data) +
          ("error" -> Messages("validation.errors")))
      },
      success = { verb =>
        transaction {
          Verb.update(verb._1)
          verb._2.foreach(n =>
            VForm.update(n._1)
          )
        }
        val successMessage = "success" -> Messages("theme.update.success", verb._1.id)
        Redirect(routes.Verbs.show(verb._1.id)).flashing(successMessage)
      }
    )
  }

  def delete(id: Long) = Action { implicit request =>
    transaction {
      VForm.removeByVerb(id)
      Verb.removeById(id)
    }
    val message = "Verbe supprimé"
    Redirect(routes.Verbs.list(request.session.get("codeLangue").get)).flashing("success" -> message)
  }

  def apiV1DateVerbs(codeL: String) = Action {
    Ok(Verb.maxUpdate(codeL))
  }

  def apiV1Verbs(codeL: String) = Action {
    val listVerbs = Verb.findAll(codeL)
      .map(t => List(JsNumber(t.id),JsString(t.in_language)))
    Ok(Json.toJson(listVerbs))
  }

  def apiV1DateForms(codeL: String) = Action {
    Ok(VForm.maxUpdate(codeL))
  }

  def apiV1Forms(codeL: String) = Action {
    val listForms = VForm.findAll(codeL)
      .map(f => List(JsNumber(f.id),JsNumber(f.verb_id),JsNumber(f.form_type_id),JsString(f.in_language)))
    Ok(Json.toJson(listForms))
  }
}
