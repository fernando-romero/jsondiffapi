organization  := "com.example"

version       := "0.1"

scalaVersion  := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  Seq(
    "io.spray"            %%  "spray-can"       % sprayV,
    "io.spray"            %%  "spray-routing"   % sprayV,
    "io.spray"            %%  "spray-json"      % "1.3.2",
    "io.spray"            %%  "spray-testkit"   % sprayV  % "it,test",
    "com.typesafe.akka"   %%  "akka-actor"      % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"    % akkaV   % "it,test",
    "org.specs2"          %%  "specs2-core"     % "2.3.11" % "it,test",
    "org.reactivemongo"   %%  "reactivemongo"   % "0.11.13",
    "ch.qos.logback"      %   "logback-classic" % "1.1.7"
  )
}

Revolver.settings

SbtScalariform.scalariformSettingsWithIt