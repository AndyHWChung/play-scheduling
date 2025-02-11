import sbt.Keys.crossScalaVersions
import sbt.{Resolver, _}

val name = "play-scheduling"

val scala2_11 = "2.11.12"
val scala2_12 = "2.12.13"

// Disable multiple project tests running at the same time: https://stackoverflow.com/questions/11899723/how-to-turn-off-parallel-execution-of-tests-for-multi-project-builds
// TODO: restrict parallelExecution to tests only (the obvious way to do this using Test scope does not seem to work correctly)
parallelExecution in Global := false

lazy val commonSettings = Seq(
  organization := "uk.gov.hmrc",
  majorVersion := 8,
  makePublicallyAvailableOnBintray := true,
  resolvers := Seq(
    Resolver.bintrayRepo("hmrc", "releases"),
    Resolver.typesafeRepo("releases"),
    "HMRC-open-artefacts-maven" at "https://open.artefacts.tax.service.gov.uk/maven2",
    Resolver.url("HMRC-open-artefacts-ivy", url("https://open.artefacts.tax.service.gov.uk/ivy2"))(Resolver.ivyStylePatterns),
    "hmrc-releases" at "https://artefacts.tax.service.gov.uk/artifactory/hmrc-releases/"
  )
)

lazy val library = (project in file("."))
  .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning, SbtArtifactory)
  .settings(
    commonSettings,
    publish := {},
    crossScalaVersions := Seq.empty
  )
  .settings(ScoverageSettings())
  .aggregate(
    playSchedulingPlay28
  ).disablePlugins(sbt.plugins.JUnitXmlReportPlugin)

lazy val playSchedulingCommon = Project("play-scheduling-common", file("play-scheduling-common"))
  .enablePlugins(SbtAutoBuildPlugin, SbtArtifactory)
  .settings(
    commonSettings,
    scalaVersion := scala2_12,
    libraryDependencies ++= AppDependencies.compileCommon ++ AppDependencies.testCommon
  )
  .settings(ScoverageSettings())

lazy val playSchedulingPlay28 = Project("play-scheduling-play-28", file("play-scheduling-play-28"))
  .enablePlugins(SbtAutoBuildPlugin, SbtArtifactory)
  .settings(
    commonSettings,
    unmanagedSourceDirectories in Compile += (playSchedulingCommon / Compile / scalaSource).value,
    unmanagedSourceDirectories in Test += (playSchedulingCommon / Test / scalaSource).value,
    scalaVersion := scala2_12,
    libraryDependencies ++= AppDependencies.compileCommon ++ AppDependencies.compilePlay28 ++ AppDependencies.testCommon ++ AppDependencies.testPlay28
  )
