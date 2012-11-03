package controllers

import play.api._
import libs.json.JsValue
import play.api.mvc._
import play.api.Play.current
import models.WebClient

/**
 * Main interface for the web page
 */
object Application extends Controller {

  private lazy val IS_WEB_ENABLED = Play.configuration.getBoolean("enable.webInterface").getOrElse(false)

  def index = Action { implicit request =>
    if(IS_WEB_ENABLED) {
      //Concurrent.hub()
      Ok(views.html.index())
    } else {
      NotFound
    }
  }


  def readStreamData =  WebSocket.async[JsValue] { request =>
      WebClient.start(request.remoteAddress)
  }
  
}