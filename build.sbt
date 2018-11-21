name := """scala-url-app"""

version := "2.6.x"

scalaVersion := "2.12.6"

crossScalaVersions := Seq("2.11.12", "2.12.4")

enablePlugins(JavaAppPackaging)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies += "org.jsoup" % "jsoup" % "1.11.3"
libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
