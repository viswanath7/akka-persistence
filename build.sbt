name := "akka-persistence"

version := "1.0"

scalaVersion := "2.12.1"

resolvers += Resolver.typesafeRepo("releases")

libraryDependencies ++= Seq(
  "org.specs2"                  %%  "specs2-core"       % "3.8.6"   % "test",
  "com.typesafe.akka"           %% "akka-actor"         % "2.4.12",
  "com.typesafe.akka"           %% "akka-persistence"   % "2.4.12",
  "com.typesafe.akka"           %% "akka-slf4j"         % "2.4.12",
  "ch.qos.logback"              % "logback-classic"     % "1.1.7",
  "org.iq80.leveldb"            % "leveldb"             % "0.7",
  "org.fusesource.leveldbjni"   % "leveldbjni-all"      % "1.8"
)

scalacOptions in Test ++= Seq("-Yrangepos")