import sbt._
import sbt.Keys._

object Build extends Build {
  lazy val root = Project("root", file("."))
    .configs(Configs.all: _*)
    .settings(Testing.settings: _*)
}