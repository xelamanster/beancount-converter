import Dependencies._

ThisBuild / scalaVersion := "2.13.2"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.github.xelamanster"

lazy val core = (project in file("core"))
  .settings(
    name := "beancount-converter-core",
    libraryDependencies ++= Seq(zio, cats),
    scalacOptions ++= Seq("-Ywarn-unused", "-deprecation")
  )
  .dependsOn(utils)

lazy val utils = (project in file("utils"))
  .settings(
    name := "beancount-converter-utils",
    libraryDependencies ++= Seq(cats, shapeless),
    scalacOptions ++= Seq("-Ywarn-unused", "-deprecation")
  )

lazy val io = (project in file("io"))
  .settings(
    name := "beancount-converter-io",
    libraryDependencies ++= Seq(cats, zio, scalaCsv, pdfobx, poiOoxml),
    scalacOptions ++= Seq("-Ywarn-unused", "-deprecation")
  )
  .dependsOn(core, utils)
