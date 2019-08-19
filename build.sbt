import Dependencies._

ThisBuild / scalaVersion     := "2.13.0"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.github.xelamanster"

lazy val root = (project in file("."))
  .settings(
    name := "beancount-converter",
    libraryDependencies ++= testDependencies
  )