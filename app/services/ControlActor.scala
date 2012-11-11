package services

import akka.actor.{PoisonPill, Props, ActorRef, Actor}
import play.api.Logger
import akka.util.Timeout
import java.util.concurrent.TimeUnit._
import play.api.libs.concurrent.Akka
import play.api.Play.current

/**
 * Created with IntelliJ IDEA.
 * User: pvillega
 * Date: 10/11/12
 * Time: 17:45
 * Service that manages the passing of messages to all the possible destinations
 */
class ControlActor extends Actor {

  implicit val timeout = Timeout(1, SECONDS)

  //Store a list of all output channels to customers
  var clients = Map.empty[String, ActorRef]

  def receive = {

    case AddClient(name: String, actor: ActorRef) => {
      Logger.info("ControlActor: Adding new client [%s]".format(name))

      if (!clients.contains(name)){
        clients = clients + (name -> actor)
      }
    }

    case MessageToStream(msg: String) => {
      notifyAll(msg)
    }

    case RemoveClient(name: String) => {
      clients = clients - name
      Logger.info("ControlActor: Client [%s] left the stream".format(name))
    }

  }

  private def notifyAll(msg: String) {
    Logger.trace("ControlActor: Sending message [%s] to connected clients [%s]".format(msg, clients))
    clients.foreach {
      case (_, actor) => actor ! MessageToStream(msg)
    }
  }

}

object ControlActor {
  private implicit val timeout = Timeout(1, SECONDS)
  private lazy val controlActor = Akka.system.actorOf(Props[ControlActor])

  def addClient(name: String, actor: ActorRef) = {
    controlActor ! AddClient(name, actor)
  }

  def removeClient(name: String) = {
    controlActor ! RemoveClient(name)
  }

  def messageToStream(msg: String) = {
    controlActor ! MessageToStream(msg)
  }

  def stop() = {
    controlActor ! PoisonPill
  }

  def isTerminated() = {
    controlActor.isTerminated
  }

}

/** Messages */
case class MessageToStream(msg: String)
case class AddClient(name: String, actor: ActorRef)
case class RemoveClient(name: String)


