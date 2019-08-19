import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.8"

  lazy val dependencies = Seq {

  }

  lazy val testDependencies = Seq {
    scalaTest
  }
}
