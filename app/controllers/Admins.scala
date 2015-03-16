package controllers

import akka.util.Crypt
import models._
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
import scala.tools.nsc.interpreter.session

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

  private def langue(codeLangue: String) = codeLangue match {
    case "it" => "italien"
    case "es" => "espagnol"
    case "an" => "anglais"
    case _ => "lingvo"
  }

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

  def chargeWords(codeLangue: String) = Action {implicit request =>
    if (request.session.get("admin") == Some("true")) {
      val responsePromiseT = WS.url("http://langues.jmarzin.fr/"+langue(codeLangue)+"/api/v2/categories")
        .get
      val responseT = Await.result(responsePromiseT, 10 seconds)
      val bodyT = Json.parse(responseT.body).as[List[JsArray]]
      Word.removeAll(codeLangue)
      Theme.removeAll(codeLangue)
      val listeThemes = bodyT.map(t => {
        val tab = t.toString().substring(1, t.toString().length - 1).split(",")
        val theme = Theme(0,
          codeLangue,
          tab(1).toInt,
          tab(2).substring(1, tab(2).length - 1),
          "")
        Theme.insert(theme)
        tab(0).toLong -> theme.id
      }).toMap

      val responsePromiseW = WS.url("http://langues.jmarzin.fr/"+langue(codeLangue)+"/api/v2/mots")
        .get
      val responseW = Await.result(responsePromiseW, 10 seconds)
      val bodyW = Json.parse(responseW.body).as[List[JsArray]]
      bodyW.foreach(w => {
        val tabW = w.toString().substring(1, w.toString().length - 1).split(",")
        Word.insert(Word(0,
          codeLangue,
          listeThemes(tabW(1).toLong),
          tabW(2).substring(1, tabW(2).length - 1),
          tabW(3).substring(1, tabW(3).length - 1),
          tabW(4).substring(1, tabW(4).length - 1),
          ""))
      })
      Ok(bodyW.toString())
    } else {
      Ok("Vous n'êtes pas administrateur")
    }
  }

  def chargeVerbs(codeLangue: String) = Action { implicit request =>
    if (request.session.get("admin") == Some("true")) {
      val responsePromiseV = WS.url("http://langues.jmarzin.fr/"+langue(codeLangue)+"/api/v2/verbes")
        .get
      val responseV = Await.result(responsePromiseV, 10 seconds)
      val bodyV = Json.parse(responseV.body).as[List[JsArray]]
      VForm.removeAll(codeLangue)
      Verb.removeAll(codeLangue)
      val listeVerbes = bodyV.map(v => {
        val tab = v.toString().substring(1, v.toString().length - 1).split(",")
        val verbe = Verb(0,
          codeLangue,
          tab(1).substring(1, tab(1).length - 1),
          ""
        )
        Verb.insert(verbe)
        tab(0).toLong -> verbe.id
      }).toMap

      val listeTypesFormes = FormType.findAll(codeLangue).map(ft => {
        ft.number -> ft.id
      }).toMap

      val responsePromiseF = WS.url("http://langues.jmarzin.fr/"+langue(codeLangue)+"/api/v2/formes")
        .get
      val responseF = Await.result(responsePromiseF, 10 seconds)
      val bodyF = Json.parse(responseF.body).as[List[JsArray]]
      bodyF.foreach(f => {
        val tabF = f.toString().substring(1, f.toString().length - 1).split(",")
        VForm.insert(VForm(0,
          codeLangue,
          listeVerbes(tabF(1).toLong),
          listeTypesFormes(tabF(2).toInt),
          tabF(3).substring(1, tabF(3).length - 1),
          ""))
      })
      Ok(bodyF.toString())
    } else {
      Ok("Vous n'êtes pas administrateur")
    }
  }
}
