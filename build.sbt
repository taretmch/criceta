import Dependencies._

ThisBuild / scalaVersion     := "2.12.11"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.criceta"
ThisBuild / organizationName := "criceta"

val commonSettings = Seq(
  resolvers ++= Seq(
    "Typesafe Releases" at "https://repo.typesafe.com/typesafe/ivy-releases/",
    "IxiaS Releases"    at "https://s3-ap-northeast-1.amazonaws.com/maven.ixias.net/releases",
  ),
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-unchecked",
    "-Xfatal-warnings",
    "-Xlint:-unused,_",
    "-Ywarn-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-unused:imports",
    "-Ywarn-inaccessible",
    "-Ywarn-nullary-override",
    "-Ywarn-numeric-widen"
  ),
  fork in Test := true,
  libraryDependencies ++= Seq(
    scalaTest % Test,
    // OSS
    "com.typesafe.play" %% "play"                 % "2.7.5",
    "net.ixias"         %% "ixias"                % "1.1.33",
    "net.ixias"         %% "ixias-play"           % "1.1.33",
    "ch.qos.logback"     % "logback-classic"      % "1.1.3"  % Test,
    "mysql"              % "mysql-connector-java" % "5.1.39" % Test
  )
)

lazy val playAuth = (project in file("play-auth"))
  .settings(name := "criceta-play-auth")
  .settings(commonSettings: _*)

lazy val docs = (project in file("docs"))
  .settings(
    mdocIn  := file("docs/src/draft"),
    mdocOut := file("docs/src/main"),
  )
 .enablePlugins(MdocPlugin)

lazy val root = (project in file("."))
  .settings(name := "criceta")
  .settings(commonSettings: _*)
  .aggregate(playAuth)
  .dependsOn(playAuth)
