import Dependencies._

ThisBuild / scalaVersion := "3.0.0-RC1"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.github.xelamanster"

scalacOptions ++= Seq("-Ywarn-unused", "-deprecation")

lazy val core = (project in file("core"))
  .settings(
    name := "beancount-converter-core",
    libraryDependencies ++= Seq(cats),
  )
  .dependsOn(utils)

lazy val utils = (project in file("utils"))
  .settings(
    name := "beancount-converter-utils",
    libraryDependencies ++= Seq(cats),
  )

lazy val io = (project in file("io"))
  .settings(
    name := "beancount-converter-io",
    libraryDependencies ++= Seq(cats, scalaCsv, pdfobx, poiOoxml),
  )
  .dependsOn(core, utils)
