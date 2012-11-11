package services

import play.api.libs.iteratee.{Concurrent, Enumerator, Enumeratee}
import play.api.libs.json.JsValue
import play.api.libs.Comet
import play.api.libs.EventSource.{EventNameExtractor, EventIdExtractor}
import akka.actor.{Props, Actor}
import akka.util.Timeout
import java.util.concurrent.TimeUnit._
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.Logger
import play.api.libs.concurrent.Akka
import play.api.Play.current

/**
 * Created with IntelliJ IDEA.
 * User: pvillega
 * Date: 05/11/12
 * Time: 19:52
 * Manages the data stream via Enumerators/Enumeratees/Iteratees
 */
object StreamManager {

  lazy val streamActor = Akka.system.actorOf(Props[StreamActor])

  // Imperative enumerator to allow push of data without closing it (no EOF unless pushed into it)
  val messages = Enumerator.imperative[String]()

  // Enumeratees to clean the fake requests
  private val cleanFakeActor : Enumeratee[String, String] = Enumeratee.map[String] { s => if(s.startsWith("FakeActor")) s.dropWhile( c => c != '[' ) else s }
  private val cleanBrackets : Enumeratee[String, String] = Enumeratee.map[String] { s => if(s.startsWith("FakeActor")) s.trim.tail.dropRight(1) else s}

  // Converts message to Json for the web version
  private val asJson: Enumeratee[String, JsValue] = Enumeratee.map[String] {
    text => JsObject(
      List(
        "eventName" -> JsString("message"),
        "text" -> JsString(text)
      )
    )
  }
  // The chain of enumerator and enumeratees that transform the input
  private val chain = messages &> cleanFakeActor ><> cleanBrackets ><> asJson

  // the Hub that let us "share" the data between all clients
  val HUB = Concurrent.hub[JsValue](chain)

  // Encoders for SSE events
  val ENCODER = Comet.CometMessage.jsonMessages
  val ID_EXTRACTOR = EventIdExtractor[JsValue]( json => (json \ "id").asOpt[String] )
  val NAME_EXTRACTOR = EventNameExtractor[JsValue]( json => (json \ "eventName").asOpt[String])

}

class StreamActor extends Actor {

  implicit val timeout = Timeout(1, SECONDS)

  def receive = {

    case MessageToStream(msg: String) => {
      Logger.trace("StreamActor: Pushing message [%s] to stream".format(msg))
      StreamManager.messages push msg
    }
  }

}