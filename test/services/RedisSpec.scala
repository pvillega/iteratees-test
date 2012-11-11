package services

import play.api.test.Helpers._
import play.api.test.FakeApplication
import org.specs2.mutable.Specification
import play.api.cache.Cache
import play.api.Play.current
import com.typesafe.plugin._
import org.sedis.Dress
import redis.clients.jedis.JedisCommands

class RedisSpec extends Specification {

  "Redis" should {

    "work as a caching mecanism" in {
      running(FakeApplication()){
        val key = "mykey"
        val data = "my data"

        Cache.set(key, data)

        val o2 = Cache.getOrElse[String](key)("fail")

        o2 mustEqual(data)

        Cache.set(key, None, 0)

        val o = Cache.getAs[String](key)

        o must beNone
      }
    }

    "store key-value pairs" in {
      running(FakeApplication()){
        val key = "storageKey"
        val data = "999"

        val pool = use[RedisPlugin].sedisPool

        pool.withJedisClient { client =>
          Dress.up(client).set(key, data)
        }

        val first = pool.withJedisClient { client =>
          Dress.up(client).get(key)
        }.getOrElse("-1")

        first mustEqual data
      }
    }

    "Return None for missing pairs" in {
      running(FakeApplication()){
        val key = "storageKey"

        val pool = use[RedisPlugin].sedisPool

        pool.withJedisClient { client =>
          client.del(key)
        }

        val second = pool.withJedisClient { client =>
          Dress.up(client).get(key)
        }

        second must beNone

      }
    }
  }

}
