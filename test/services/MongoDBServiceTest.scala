package services

import akka.actor.{ActorSystem}
import akka.util.{Timeout}
import java.util.concurrent.TimeUnit._
import akka.testkit.{TestKit, TestActorRef}
import play.api.test.Helpers._
import play.api.test.FakeApplication
import org.specs2.mutable.Specification
import models.Packet
import com.mongodb.casbah.commons.MongoDBObject
import java.util.Date


class MongoDBServiceTest extends TestKit(ActorSystem("test")) with Specification {

  private implicit val timeout = Timeout(1, SECONDS)

  "MongoDBActor" should {

    "Push messages to mongo DB" in {
      running(FakeApplication()){
        val msg = "Test message"
        val mongoActor = TestActorRef(new MongoDBActor)

        mongoActor ! MessageToStream(msg)

        val pckt = Packet.dao.findOne(MongoDBObject("message" -> msg)).getOrElse(new Packet(message = "", time = new Date()))
        pckt.message mustEqual(msg)
      }
    }
  }

}
