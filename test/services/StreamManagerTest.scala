package services

import org.specs2.mutable._
import akka.actor.{ActorSystem}
import akka.util.{Timeout}
import java.util.concurrent.TimeUnit._
import akka.testkit.{TestKit, TestActorRef}
import play.api.test.Helpers._
import play.api.test.FakeApplication

class StreamManagerTest extends TestKit(ActorSystem("test")) with Specification {

  private implicit val timeout = Timeout(1, SECONDS)

  "StreamActor" should {

    "Push messages to stream manager" in {
      running(FakeApplication()){
        val msg = "Test message"
        val streamActor = TestActorRef(new StreamActor)

        streamActor ! MessageToStream(msg)


        StreamManager.HUB.getPatchCord().map{ e =>
          (e \ "eventName") mustEqual("message")
          (e \ "text") mustEqual(msg)
        }

        StreamManager.HUB.closed() must beFalse
      }
    }
  }

}
