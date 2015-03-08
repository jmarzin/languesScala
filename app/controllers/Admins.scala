package controllers

import akka.util.Crypt
import models.{Word, Theme, Admin}
import play.api.Play.current
import play.api.data.Form
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsArray, Json}
import scala.concurrent.duration._
import play.api.data.Forms.{mapping, nonEmptyText}
import play.api.i18n.Messages
import play.api.libs.ws.WS
import play.api.mvc.{Flash, Action, Controller}
import java.lang.String._


import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * Created by jacques on 25/02/15.
 */
object Admins extends Controller{

  private val adminForm: Form[Admin] = Form(
    mapping(
      "password" -> nonEmptyText.verifying(
        "Mot de passe erroné ", Crypt.md5(_) == "38B5188C3032225C37392EF863057344")
    )(models.Admin.apply)(Admin.unapply)
  )

  val foo = adminForm.copy()

  def password = Action { implicit request =>
    val form = if (request.flash.get("error").isDefined)
      adminForm.bind(request.flash.data)
    else
      adminForm

    Ok(views.html.admins.admin(form))
  }

  def checkPassword = Action { implicit request =>
    val newAdminForm = adminForm.bindFromRequest()
    newAdminForm.fold(
      hasErrors = { form =>
        Redirect(routes.Admins.password()).
          flashing(Flash(form.data) +
          ("error" -> Messages("validation.errors")))
      },
      success = { success =>
        val message = "Vous êtes administrateur"
        Redirect(routes.Themes.list(request.session.get("codeLangue").get)).flashing("success" -> message)
          .withSession(request.session - "admin" + ("admin" -> "true"))
      }
    )
  }

  def charge = Action {implicit request =>
    val responsePromiseT = WS.url("http://langues.jmarzin.fr/italien/api/v2/categories")
      .get
    val responseT = Await.result(responsePromiseT, 10 seconds)
    val bodyT = Json.parse(responseT.body).as[List[JsArray]]
    Word.removeAll("it")
    Theme.removeAll("it")
    val listeThemes = bodyT.map(t => {
      val tab = t.toString().substring(1, t.toString().length - 1).split(",")
      val theme = Theme(0,
        "it",
        tab(1).toInt,
        tab(2).substring(1,tab(2).length - 1),
        "")
      Theme.insert(theme)
      tab(0).toLong -> theme.id
    }).toMap

    val responsePromiseW = WS.url("http://langues.jmarzin.fr/italien/api/v2/mots")
      .get
    val responseW = Await.result(responsePromiseW, 10 seconds)
    val bodyW = Json.parse(responseW.body).as[List[JsArray]]
    bodyW.foreach(w =>
    {val tabW = w.toString().substring(1,w.toString().length-1).split(",")
      Word.insert(Word(0,
        "it",
        listeThemes(tabW(1).toLong),
        tabW(2).substring(1,tabW(2).length - 1),
        tabW(3).substring(1,tabW(3).length - 1),
        tabW(4).substring(1,tabW(4).length - 1),
        ""))})
   Ok(bodyW.toString())
  }

}
