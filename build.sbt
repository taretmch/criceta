import Dependencies._

ThisBuild / scalaVersion     := "2.13.3"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.github.taretmch"
ThisBuild / organizationName := "taretmch"

lazy val core = project
  .settings(
    name := "criceta",
    libraryDependencies ++= Seq(
      scalaTest % Test
    )
  )

lazy val docs = project
  .settings(
    mdocIn  := file("docs/src/draft"),
    mdocOut := file("docs/src/main"),
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.1" cross CrossVersion.full)
  )
  .dependsOn(core)
  .enablePlugins(MdocPlugin)
