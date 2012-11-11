package controllers

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json._
import play.api.test.FakeApplication
import play.api.libs.json.JsString
import services.Config

class ApplicationSpec extends Specification {

  "Application" should {
    "respond to the index Action if web is enabled " in {
      running(FakeApplication(additionalConfiguration = Map("enable.webInterface" -> "true") )) {

        val result = controllers.Application.index()(FakeRequest())

        status(result) must equalTo(OK)
        contentType(result) must beSome("text/html")
        charset(result) must beSome("utf-8")
        contentAsString(result) must contain("Welcome to the Iteratees Sample")
      }
    }

    "return 404 on the index Action if web is disabled " in {
      running(FakeApplication(additionalConfiguration = Map(("enable.webInterface" -> "false")) )) {

        Config.IS_WEB_ENABLED must beFalse

        val result = controllers.Application.index()(FakeRequest())

        status(result) must equalTo(NOT_FOUND)
        contentType(result) must beSome("text/html")
        charset(result) must beSome("utf-8")
      }
    }

    "return BadRequest if POST to app is not Json data" in {
        val result = controllers.Application.receiveData()(FakeRequest())

        status(result) must equalTo(BAD_REQUEST)
    }

    "return BadRequest if POST to app is missing message" in {
        val node = JsString("wrong_json")
        val result = controllers.Application.receiveData()(FakeRequest().withJsonBody(node))

        status(result) must equalTo(BAD_REQUEST)
    }

    "return Ok if POST to app is succesful" in {
        val node = toJson(Map("data" -> toJson("a test message")))
        val result = controllers.Application.receiveData()(FakeRequest().withJsonBody(node))

        status(result) must equalTo(OK)
    }

    "Stream is a chucked repsonse" in {

      val result = controllers.Application.stream()(FakeRequest())

      status(result) must equalTo(OK)
      contentType(result) must beSome("text/event-stream")
    }

  }
}