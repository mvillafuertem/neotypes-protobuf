ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name                 := "neotypes-protobuf",
    libraryDependencies ++= Seq(
      "org.neo4j.driver"    % "neo4j-java-driver"    % "5.4.0",
      "io.github.neotypes" %% "neotypes-cats-effect" % "0.23.1",
      "io.github.neotypes" %% "neotypes-generic"     % "0.23.1",
      "org.typelevel"      %% "cats-effect"          % "3.3.5"
    ),
    Compile / PB.targets := Seq(
      scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
    )
  )
