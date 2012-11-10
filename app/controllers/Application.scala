package controllers

import play.api._
import libs.{EventSource}
import play.api.mvc._
import scala.Some
import services.{ControlActor, StreamManager}
import services.Config._

/**
 * Main interface for the web page
 */
object Application extends Controller {

  def index = Action { implicit request =>
    if(IS_WEB_ENABLED) {
      Ok(views.html.index())
    } else {
      NotFound
    }
  }

  /**
   * Post request that receives the data to redirect
   */
  def receiveData() = Action { implicit request =>
    request.body.asJson match {
      case Some(jsValue) =>{
        val msg = (jsValue \ "data").asOpt[String]
        Logger.trace("Application.receiveData - Message retrieved [%s]".format(msg))
        msg match {
          case Some(s) => {
            ControlActor.messageToStream(s)
            Ok
          }
          case _ => {
            Logger.warn("Application.receiveData - Json content missing data element")
            BadRequest("Json content missing [data] element")
          }
        }
      }
      case _ => {
        Logger.warn("Application.receiveData - request received without Json body")
        BadRequest("Not a Json body in request")
      }
    }
  }

  // SSE stream to any client connected
  def stream = Action { implicit request =>

    implicit val encoder = StreamManager.ENCODER
    implicit val eventIdExtractor = StreamManager.ID_EXTRACTOR
    implicit val eventNameExtractor = StreamManager.NAME_EXTRACTOR

    Ok.stream(StreamManager.HUB.getPatchCord &> EventSource()).as("text/event-stream")
  }
  
}