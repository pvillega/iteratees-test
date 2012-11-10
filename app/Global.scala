
import akka.util.{Timeout, Duration}
import controllers.routes
import java.util.concurrent.TimeUnit._
import play.api._
import libs.concurrent.Akka
import libs.json.Json._
import libs.ws.WS
import services.{StreamManager, ControlActor}
import util.Random
import akka.actor._
import play.api.Play.current
import services.Config._


/**
 * Created with IntelliJ IDEA.
 * User: pvillega
 * Date: 05/11/12
 * Time: 07:48
 * Initializes the application
 */
object Global extends GlobalSettings {

  private lazy val fakeStreamActor = Akka.system.actorOf(Props[FakeStreamActor])
  private val STREAM_ACTOR = "streamWeb"

  override def onStart(app: Application) = {
    Logger.info("Global: App starting, setting actors")

    if (IS_STREAM_ENABLED) {
      Logger.info("Global: Starting Fake Request actor")

      Akka.system.scheduler.schedule(
        Duration(500, MILLISECONDS),
        Duration(500, MILLISECONDS),
        fakeStreamActor,
        Data("FakeActor Data [text]"))
    }

    if(IS_WEB_ENABLED) {
      Logger.info("Global: Starting Web Stream (SSE) actor")

      ControlActor.addClient(STREAM_ACTOR, StreamManager.streamActor)
    }
  }


  override def onStop(app: Application) = {
    Logger.info("Global: App stopping, closing actors")

    if (IS_STREAM_ENABLED) {
      Logger.info("Global: Stopping Fake Request actor")

      fakeStreamActor ! PoisonPill
    }

    if(IS_WEB_ENABLED) {
      Logger.info("Global: Stopping Web Stream (SSE) actor")

      ControlActor.removeClient(STREAM_ACTOR)
      StreamManager.streamActor ! PoisonPill
    }

    ControlActor.stop()
  }

}

/**
 * Fake actor to generate data for the POST request
 */
class FakeStreamActor extends Actor {

  implicit val timeout = Timeout(1, SECONDS)

  def receive = {

    case Data(msg: String) => {
      Logger.trace("FakeStreamActor: received data [%s]".format(msg))

      //send data via POST request to application
      val jsonBody = toJson(Map("data" -> toJson(msg)))
      WS.url(SERVER_LOCATION + routes.Application.receiveData.url).post(jsonBody).map { case response =>
         Logger.trace("FakeStreamActor: received response [%d] [%s]".format(response.status, response.body))
      }
    }
  }

}

/** Messages */
case class Data(msg: String)