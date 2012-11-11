import sbt._
import Keys._
import PlayProject._
import de.johoop.jacoco4sbt.JacocoPlugin._

object ApplicationBuild extends Build {

    val appName         = "iterateesTest"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
      "com.typesafe.akka" % "akka-testkit" % "2.0" % "test",
      //MongoDB connector
      "se.radley" %% "play-plugins-salat" % "1.1"
    )

    lazy val s = Defaults.defaultSettings ++ Seq(jacoco.settings:_*)

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA, settings = s).settings(
      // MongoDB Salat
      routesImport += "se.radley.plugin.salat.Binders._",
      templatesImport += "org.bson.types.ObjectId",
      //Jacoco test
      parallelExecution in jacoco.Config := false

    )

}
