import sbt._

object Dependencies {

  lazy val cats = "org.typelevel" %% "cats-core" % "2.1.0"
  lazy val zio = "dev.zio" %% "zio" % "1.0.0-RC11-1"

  lazy val scalaCsv = "com.github.tototoshi" %% "scala-csv" % "1.3.6"
  lazy val pdfobx = "org.apache.pdfbox" % "pdfbox" % "2.0.16"
  lazy val poiOoxml = "org.apache.poi" % "poi-ooxml" % "4.1.0"

  lazy val shapeless = "com.chuusai" %% "shapeless" % "2.3.3"

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.8"
}
