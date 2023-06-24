ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name                 := "neotypes-protobuf",
    libraryDependencies ++= Seq(
      "org.neo4j.driver"        % "neo4j-java-driver"    % "5.6.0",
      "org.neo4j"               % "neo4j"                % "5.6.0",
      "io.github.neotypes"     %% "neotypes-cats-effect" % "1.0.0-M1",
      "io.github.neotypes"     %% "neotypes-generic"     % "1.0.0-M1",
      "org.typelevel"          %% "cats-effect"          % "3.4.8",
      "io.circe"               %% "circe-core"           % "0.14.5",
      "io.circe"               %% "circe-generic"        % "0.14.5",
      "io.github.scalapb-json" %% "scalapb-circe"        % "0.12.2"
    ) ++ Seq(
      "org.scalatest"          %% "scalatest"            % "3.2.15" % Test
    ) ++ Seq(
      "org.scala-lang" % "scala-reflect"  % scalaVersion.value,
      "org.scala-lang" % "scala-compiler" % scalaVersion.value % Test
    ),
    Compile / PB.targets := Seq(
      scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
    ),
    //scalacOptions += "-Ymacro-debug-lite"
  )
