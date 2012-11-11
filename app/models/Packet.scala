package models

import play.api.Play.current
import java.util.Date
import com.novus.salat.global._ //global context, don't remove
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._

case class Packet(
  id: ObjectId = new ObjectId,
  time: Date,
  message: String
  )

object Packet extends ModelCompanion[Packet, ObjectId] {
  val dao = new SalatDAO[Packet, ObjectId](collection = mongoCollection("messages")) {}

  def findByDate(date: Date): Option[Packet] = dao.findOne(MongoDBObject("date" -> date))
}