import sbt._

object Dependencies {

  lazy val cats = "org.typelevel" %% "cats-core" % "2.6.1"

  lazy val scalaCsv = "com.github.tototoshi" %% "scala-csv" % "1.3.8"
  lazy val pdfobx = "org.apache.pdfbox" % "pdfbox" % "2.0.16"
  lazy val poiOoxml = "org.apache.poi" % "poi-ooxml" % "4.1.0"
}
