package controllers

import models.{Word,Theme}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.libs.json.{Json, JsString, JsNumber}
import play.api.mvc.{Flash, Action, Controller}

/**
 * Created by jacques on 25/02/15.
 */
object Words extends Controller {

  private def wordForm = Form(
    mapping(
      "id" -> longNumber,
      "language_id" -> text,
      "theme_id" -> longNumber(min = 1L),
      "in_french" -> nonEmptyText,
      "sort_word" -> nonEmptyText,
      "in_language" -> nonEmptyText,
      "last_update" -> text
    )(Word.apply)(Word.unapply)
  )

  def list(codeLangue: String, page: Int) = Action { implicit request =>
    if (request.session.isEmpty)
      Redirect(routes.Themes.changeLanguage("italien"))
    else {
      val liste = Word.findAll(codeLangue)
      var pageCourante = page
      val maxpage:Int = ((liste.size - 1) /15) + 1
      if (pageCourante > maxpage) {
        pageCourante = maxpage
      } else if (page < 1) {
        pageCourante = 1
      }
      val indicPrec = (pageCourante > 1)
      val indicSuiv = (pageCourante < maxpage)
      Ok(views.html.words.list(
        liste.dropRight(math.max(0, liste.size - pageCourante * 15)).drop((pageCourante - 1) * 15),
        indicPrec, indicSuiv, pageCourante))
    }
  }

  def newWord(codeLangue: String) = Action { implicit request =>
    val form = if (request.flash.get("error").isDefined) {
      val errorForm = this.wordForm.bind(request.flash.data)
      errorForm
    } else {
      val newWord = Word(0,
        codeLangue,
        request.session.get("theme_id").getOrElse("0").toLong,
        "",
        "",
        "",
        ""
      )
      this.wordForm.fill(newWord)
    }

    Ok(views.html.words.editWord(form))
  }

  def save(codeLangue: String) = Action { implicit request =>
    val newWordForm = this.wordForm.bindFromRequest()
    newWordForm.fold(
      hasErrors = { form =>
        Redirect(routes.Words.newWord(request.session.get("codeLangue").get)).flashing(Flash(form.data) +
          ("error" -> Messages("validation.errors")))
      },
      success = { newWord =>
        Word.insert(newWord)
        val successMessage = ("success" -> Messages("theme.new.success", newWord.id))
        Redirect(routes.Words.show(newWord.id))
          .flashing(successMessage)
          .withSession(request.session - "theme_id" + ("theme_id" -> newWord.theme_id.toString))
      }
    )
  }

  def show(id: Long) = Action { implicit request =>
    Word.findById(id).map { word =>
      Ok(views.html.words.details(word))
    }.getOrElse(NotFound)
  }

  def edit(id: Long) = Action { implicit request =>
    Word.findById(id).map { word =>
      Ok(views.html.words.editWord(this.wordForm.fill(word), Option(id)))
    }.getOrElse(NotFound)
  }

  def update(id: Long) = Action { implicit request =>
    val newWordForm = this.wordForm.bindFromRequest()
    newWordForm.fold(
      hasErrors = { form =>
        Ok(views.html.words.editWord(form, Option(id))).flashing(Flash(form.data) +
          ("error" -> Messages("validation.errors")))
      },
      success = { word =>
        Word.update(word)
        val successMessage = ("success" -> Messages("word.update.success", word.id))
        Redirect(routes.Words.show(word.id)).flashing(successMessage)
      }
    )
  }

  def delete(id: Long) = Action { implicit request =>
    Word.removeById(id)
    val message = "Mot supprimé"
    Redirect(routes.Words.list(request.session.get("codeLangue").get)).flashing("success" -> message)
  }

  def apiV1DateWords(codeL: String) = Action {
    Ok(Word.maxUpdate(codeL))
  }

  def apiV1Words(codeL: String) = Action {
    val listWords = Word.findAll(codeL)
      .map(t => List(JsNumber(t.id),
        JsNumber(t.theme_id),
        JsString(t.in_french),
        JsString(t.sort_word),
        JsString(t.in_language)))
    Ok(Json.toJson(listWords))
  }
}
