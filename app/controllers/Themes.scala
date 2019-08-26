package controllers

import models.Theme
import play.api.libs.json.Json
import play.api.libs.json._
import play.api.mvc.{Action, Flash, Controller}
import play.api.data.Form
import play.api.data.Forms.{mapping, longNumber, number, text, nonEmptyText}
import play.api.i18n.Messages

/**
 * Created by jacques on 25/02/15.
 */
object Themes extends Controller {

  private def themeForm = Form(
    mapping(
      "id" -> longNumber,
      "language_id" -> text,
      "number" -> number(min = 1),
      "in_language" -> nonEmptyText,
      "last_update" -> text,
      "supp" -> text
    )(Theme.apply)(Theme.unapply)
  )

  def list(codeLangue: String, page: Int) = Action { implicit request =>
    if (request.session.isEmpty)
      Redirect(routes.Themes.changeLanguage("italien"))
    else {
      val liste = Theme.findAll(codeLangue)
      var pageCourante = page
      val maxpage:Int = ((liste.size - 1) /15) + 1
      if (pageCourante > maxpage) {
        pageCourante = maxpage
      } else if (page < 1) {
        pageCourante = 1
      }
      val indicPrec = pageCourante > 1
      val indicSuiv = pageCourante < maxpage
      Ok(views.html.themes.list(
        liste.dropRight(math.max(0, liste.size - pageCourante * 15)).drop((pageCourante - 1) * 15),
        indicPrec, indicSuiv, pageCourante))
    }
  }

  def changeLanguage(langue: String) = Action { implicit request =>
    val message = "Vous travaillez maintenant en " + langue
    Redirect(routes.Themes.list(langue.substring(0, 2))).flashing("success" -> message)
      .withSession(request.session - "langue" + ("langue" -> langue)
      - "codeLangue" + ("codeLangue" -> langue.substring(0, 2)))
  }

  def newTheme(codeLangue: String) = Action { implicit request =>
    val form = if (request.flash.get("error").isDefined) {
      val errorForm = this.themeForm.bind(request.flash.data)
      errorForm
    } else {
      val newTheme = Theme(0,
        codeLangue,
        Theme.lastNumber(codeLangue) + 1,
        "",
        "",
        "f"
      )
      this.themeForm.fill(newTheme)
    }
    Ok(views.html.themes.editTheme(form))
  }

  def save(codeLangue: String) = Action { implicit request =>
    val newThemeForm = this.themeForm.bindFromRequest()
    newThemeForm.fold(
      hasErrors = { form =>
        Redirect(routes.Themes.newTheme(request.session.get("codeLangue").get)).flashing(Flash(form.data) +
          ("error" -> Messages("validation.errors")))
      },
      success = { newTheme =>
        Theme.insert(newTheme)
        val successMessage = "success" -> Messages("theme.new.success", newTheme.id)
        Redirect(routes.Themes.show(newTheme.id)).flashing(successMessage)
      }
    )
  }

  def show(id: Long) = Action { implicit request =>
    Theme.findById(id).map { theme =>
      Ok(views.html.themes.details(theme))
    }.getOrElse(NotFound)
  }

  def edit(id: Long) = Action { implicit request =>
    Theme.findById(id).map { theme =>
      Ok(views.html.themes.editTheme(this.themeForm.fill(theme), Option(id)))
    }.getOrElse(NotFound)
  }

  def update(id: Long) = Action { implicit request =>
    val newThemeForm = this.themeForm.bindFromRequest()
    newThemeForm.fold(
      hasErrors = { form =>
        Ok(views.html.themes.editTheme(form, Option(id))).flashing(Flash(form.data) +
          ("error" -> Messages("validation.errors")))
      },
      success = { theme =>
        Theme.update(theme)
        val successMessage = "success" -> Messages("theme.update.success", theme.id)
        Redirect(routes.Themes.show(theme.id)).flashing(successMessage)
      }
    )
  }

  def delete(id: Long) = Action { implicit request =>
    Theme.removeById(id)
    val message = "Thème supprimé"
    Redirect(routes.Themes.list(request.session.get("codeLangue").get)).flashing("success" -> message)
  }

  def apiV1DateThemes(codeL: String) = Action {
    Ok(Theme.maxUpdate(codeL))
  }

  def apiV1Themes(codeL: String) = Action {
    val listThemes = Theme.findAll(codeL)
      .map(t => List(JsNumber(t.id),JsNumber(t.number),JsString(t.in_language)))
    Ok(Json.toJson(listThemes))
  }
}
