package controllers

import models.Verb
import play.api.mvc.{Action, Controller}

/**
 * Created by jacques on 25/02/15.
 */
object Verbs extends Controller{
  def list = Action { implicit request =>
    Ok(views.html.verbs.list(Verb.findAll))
  }
}
