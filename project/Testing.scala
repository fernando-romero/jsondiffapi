import sbt._
import sbt.Keys._

object Testing {
  lazy val testAll = TaskKey[Unit]("test-all")

  private lazy val itSettings =
    inConfig(IntegrationTest)(Defaults.testSettings) ++
    Seq(
      fork in IntegrationTest := false,
      parallelExecution in IntegrationTest := false,
      scalaSource in IntegrationTest := baseDirectory.value / "src/it/scala")

  lazy val settings = itSettings ++ Seq(
    testAll <<= (test in IntegrationTest).dependsOn(test in Test)
  )
}