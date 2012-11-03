package models

import akka.actor._

import play.api._
import play.api.libs.json._
import play.api.libs.iteratee._
import play.api.libs.concurrent._

import akka.util.{Duration, Timeout}
import java.util.concurrent.TimeUnit._
import akka.pattern.ask

import play.api.Play.current



/**
 * Created with IntelliJ IDEA.
 * User: pvillega
 * Date: 03/11/12
 * Time: 18:46
 * Provides the Websocket functionality to stream data to the web page
 */
object WebClient {

  implicit val timeout = Timeout(1, SECONDS)

  lazy val webactor = Akka.system.actorOf(Props[WebActor])

  AutoCheck(webactor)

  def start(user: String): Promise[(Iteratee[JsValue, _], Enumerator[JsValue])] = {

    (webactor ? Join(user)).map {

      case Connected(enumerator) =>

        // Create an Iteratee to consume the feed
        val iteratee = Iteratee.foreach[JsValue] { event =>
          Logger.info("Actor [%s] sent event %s".format(user, event))
          //webactor ! Data(user)
        }.mapDone { _ =>
          webactor ! Quit(user)
        }

        //return the 2 channels, in and out
        (iteratee,enumerator)
    }.asPromise
  }
}

object AutoCheck {

  def apply(webactor: ActorRef) {

    // Create an Iteratee that log all messages to the console.
    val loggerIteratee = Iteratee.foreach[JsValue](event => Logger("robot").info(event.toString))

    implicit val timeout = Timeout(1, SECONDS)

    // Make the robot join the room
    webactor ? (Join("Robot")) map {
      case Connected(robotChannel) =>
        // Apply this Enumerator on the logger.
        robotChannel |>> loggerIteratee
    }

    // Make the robot talk every 30 seconds
    Akka.system.scheduler.schedule(
      Duration(30, SECONDS),
      Duration(30, SECONDS),
      webactor,
      Data("I'm still alive")
    )
  }

}


class WebActor extends Actor {

  implicit val timeout = Timeout(1, SECONDS)

  //Store a list of all output channels to customers
  var members = Map.empty[String, PushEnumerator[JsValue]]

  def receive = {

    case Join(user: String) => {
      // Create an Enumerator to write to this socket
      val channel =  Enumerator.imperative[JsValue](onStart = onStart(user), onComplete = onComplete(user) )

      if (!members.contains(user)){
        members = members + (user -> channel)
      }

      sender ! Connected(channel)
    }

    case Data(data) => {
      notifyAll(data)
    }

    case Quit(user) => {
      members = members - user
      Logger.info("Actor [%s] left the web stream".format(user))
    }

  }

  private def notifyAll(text: String) {
    val msg = JsObject(
      Seq(
        "message" -> JsString(text)
      )
    )

    members.foreach {
      case (_, channel) => channel.push(msg)
    }
  }

  private def onStart(user: String) = {
    Logger.info("Actor [%s] joined the web stream".format(user))
  }

  private def onComplete(user: String) = {
    Logger.info("Actor [%s] stream is complete".format(user))
  }

  private def onError(user: String, input: Input[JsValue]) = {
    Logger.error("Actor [%s] stream has failed on error: %s".format(user, input.toString))
  }

}

/* Valid messages below */

case class Join(user: String)
case class Quit(user: String)
case class Data(data: String)

case class Connected(enumerator:Enumerator[JsValue])