package services

import play.api.Play
import play.api.Play.current

/**
 * Created with IntelliJ IDEA.
 * User: pvillega
 * Date: 09/11/12
 * Time: 19:47
 * Unifies access to configuration values
 */
object Config {

  lazy val IS_WEB_ENABLED = Play.configuration.getBoolean("enable.webInterface").getOrElse(false)

  lazy val IS_STREAM_ENABLED = Play.configuration.getBoolean("fakeStream.enable").getOrElse(false)

  lazy val SERVER_LOCATION = Play.configuration.getString("fakeStream.server").getOrElse("http://localhost:9000/")

  lazy val IS_MONGO_ENABLED = Play.configuration.getBoolean("mongo.stream.enable").getOrElse(false)
}
