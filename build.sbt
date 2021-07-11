name := "kafka-tweets-scala"
organization := "com.github.kafka-tweets"
version := "0.1"
scalaVersion := "2.13.6"

resolvers ++= Seq(
  "Confluent" at "https://packages.confluent.io/maven/"
)

// https://mvnrepository.com/artifact/org.apache.kafka/kafka-streams-scala_2.13/2.8.0
// Note: %% appends scala version (2.13) to the search url
libraryDependencies ++= Seq(
  "org.apache.kafka" %% "kafka-streams-scala" % "2.8.0",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.12.4",
  "io.confluent" % "kafka-json-serializer" % "6.2.0",
  "org.slf4j" % "slf4j-api" % "1.7.31",
  "org.slf4j" % "slf4j-log4j12" % "1.7.31"
)

// leverage java 8
javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")
scalacOptions := Seq("-target:jvm-1.8")
initialize := {
  val _ = initialize.value
  if (sys.props("java.specification.version") != "1.8")
    sys.error("Java 8 is required for this project.")
}