package services

import org.specs2.mutable._
import play.api.libs.concurrent.{Akka}
import akka.actor.{ActorSystem, Actor}
import akka.util.{Timeout}
import java.util.concurrent.TimeUnit._
import akka.testkit.{TestKit, TestActorRef}
import play.api.test.Helpers._
import play.api.test.FakeApplication


class ControlActorTest extends TestKit(ActorSystem("test")) with Specification {

  private implicit val timeout = Timeout(1, SECONDS)

  "ControlActor" should {

    "Add client when told" in {
      running(FakeApplication()){
        val controlActor = TestActorRef(new ControlActor)
        val controlActorReal = controlActor.underlyingActor
        val testActor = TestActorRef(new TestActor)

        controlActor ! AddClient("test", testActor)

        controlActorReal.clients must not beEmpty

        controlActorReal.clients must haveKey("test")
      }
    }

    "Remove client when told" in {
      running(FakeApplication()){
        val controlActor = TestActorRef(new ControlActor)
        val controlActorReal = controlActor.underlyingActor
        val testActor = TestActorRef(new TestActor)

        controlActor ! AddClient("test", testActor)
        controlActor ! RemoveClient("test")

        controlActorReal.clients must beEmpty

        controlActorReal.clients must not haveKey("test")
      }
    }

    "Must propagate messages" in {
      running(FakeApplication()){
        val msg = "test message"
        val controlActor = TestActorRef(new ControlActor)
        val controlActorReal = controlActor.underlyingActor
        val testActor = TestActorRef(new TestActor)
        val testActorReal = testActor.underlyingActor

        controlActor ! AddClient("test", testActor)
        controlActor ! MessageToStream(msg)
        controlActor ! RemoveClient("test")

        testActorReal.received must not beEmpty

        testActorReal.received.length must be_==(1)

        testActorReal.received.contains(msg) must beTrue
      }
    }

  }

  system.shutdown() //to avoid elaking Akka actors
}

// Actor for testing purposes
class TestActor extends Actor {

  var received = List[String]()

  def receive = {

    case MessageToStream(msg: String) => {
      received = msg :: received
    }
  }

}

