package services

import play.api.libs.concurrent.Akka
import akka.actor.{Actor, Props}
import akka.util.Timeout
import java.util.concurrent.TimeUnit._
import play.api.Logger
import models.Packet
import java.util.Date
import play.api.Play.current

/**
 * Created with IntelliJ IDEA.
 * User: pvillega
 * Date: 11/11/12
 * Time: 20:11
  * Stores messages into MongoDB
 */
object MongoDBService {

  lazy val mongoActor = Akka.system.actorOf(Props[MongoDBActor])

}

class MongoDBActor extends Actor {

  implicit val timeout = Timeout(1, SECONDS)

  def receive = {

    case MessageToStream(msg: String) => {
      Logger.trace("MongoDBActor: Pushing message [%s] to mongoDB".format(msg))
      val message = new Packet(message = msg, time = new Date())
      Packet.save(message)

      //Send also a message to stream if enabled to notify the UI that mongo is getting messages
      if(Config.IS_STREAM_ENABLED) {
        StreamManager.streamActor ! MessageToStream("MongoDB [%s]".format(msg))
      }
    }
  }

}