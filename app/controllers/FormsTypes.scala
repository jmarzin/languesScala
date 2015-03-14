package controllers

import models.FormType
import org.joda.time.{DateTimeZone, DateTime}
import org.joda.time.format.{DateTimeFormat, ISODateTimeFormat}
import play.api.libs.json.Json
import play.api.libs.json._
import play.api.mvc.{Action, Flash, Controller}
import play.api.data.Form
import play.api.data.Forms.{mapping, longNumber, number, text, nonEmptyText}
import play.api.i18n.Messages


/**
 * Created by jacques on 25/02/15.
 */
object FormsTypes extends Controller {

  private def formTypeForm = Form(
    mapping(
      "id" -> longNumber,
      "language_id" -> text,
      "number" -> number(min = 1),
      "in_french" -> nonEmptyText,
      "last_update" -> text
    )(FormType.apply)(FormType.unapply)
  )

  def list(codeLangue: String, page: Int) = Action { implicit request =>
    if (request.session.isEmpty)
      Redirect(routes.Themes.changeLanguage("italien"))
    else {
      val liste = FormType.findAll(codeLangue)
      var pageCourante = page
      val maxpage:Int = ((liste.size - 1) /15) + 1
      if (pageCourante > maxpage) {
        pageCourante = maxpage
      } else if (page < 1) {
        pageCourante = 1
      }
      val indicPrec = (pageCourante > 1)
      val indicSuiv = (pageCourante < maxpage)
      Ok(views.html.formsTypes.list(
        liste.dropRight(math.max(0, liste.size - pageCourante * 15)).drop((pageCourante - 1) * 15),
        indicPrec, indicSuiv, pageCourante))
    }
  }

  def newFormType(codeLangue: String) = Action { implicit request =>
    val form = if (request.flash.get("error").isDefined) {
      val errorForm = this.formTypeForm.bind(request.flash.data)
      errorForm
    } else {
      val newFormType = FormType(0,
        codeLangue,
        FormType.lastNumber(codeLangue) + 1,
        "",
        ""
      )
      this.formTypeForm.fill(newFormType)
    }
    Ok(views.html.formsTypes.editFormType(form))
  }

  def save(codeLangue: String) = Action { implicit request =>
    val newFormTypeForm = this.formTypeForm.bindFromRequest()
    newFormTypeForm.fold(
      hasErrors = { form =>
        Redirect(routes.FormsTypes.newFormType(request.session.get("codeLangue").get)).flashing(Flash(form.data) +
          ("error" -> Messages("validation.errors")))
      },
      success = { newFormType =>
        FormType.insert(newFormType)
        val successMessage = ("success" -> Messages("formType.new.success", newFormType.id))
        Redirect(routes.FormsTypes.show(newFormType.id)).flashing(successMessage)
      }
    )
  }

  def show(id: Long) = Action { implicit request =>
    FormType.findById(id).map { formType =>
      Ok(views.html.formsTypes.details(formType))
    }.getOrElse(NotFound)
  }

  def edit(id: Long) = Action { implicit request =>
    FormType.findById(id).map { formType =>
      Ok(views.html.formsTypes.editFormType(this.formTypeForm.fill(formType), Option(id)))
    }.getOrElse(NotFound)
  }

  def update(id: Long) = Action { implicit request =>
    val newFormTypeForm = this.formTypeForm.bindFromRequest()
    newFormTypeForm.fold(
      hasErrors = { form =>
        Ok(views.html.formsTypes.editFormType(form, Option(id))).flashing(Flash(form.data) +
          ("error" -> Messages("validation.errors")))
      },
      success = { formType =>
        FormType.update(formType)
        val successMessage = ("success" -> Messages("formType.update.success", formType.id))
        Redirect(routes.FormsTypes.show(formType.id)).flashing(successMessage)
      }
    )
  }

  def delete(id: Long) = Action { implicit request =>
    FormType.removeById(id)
    val message = "Forme supprimée"
    Redirect(routes.FormsTypes.list(request.session.get("codeLangue").get)).flashing("success" -> message)
  }

  def apiV1DateThemes(codeL: String) = Action {
    Ok(FormType.maxUpdate(codeL))
  }

  def apiV1Themes(codeL: String) = Action {
    val listFormsTypes = FormType.findAll(codeL)
      .map(ft => List(JsNumber(ft.id),JsNumber(ft.number),JsString(ft.in_french)))
    Ok(Json.toJson(listFormsTypes))
  }
}
