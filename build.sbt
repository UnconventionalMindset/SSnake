import xsbti.compile.CompileOptions

organization := "io.github.jacopogobbi"

name := "SSnake"

version := "0.2"

scalaVersion := "3.1.1"

// Add dependency on ScalaFX library
libraryDependencies += "org.scalafx" %% "scalafx" % "17.0.1-R26"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-feature")

mainClass := Some("io.github.jacopogobbi.ssnake.SSnake")

// Fork a new JVM for 'run' and 'test:run', to avoid JavaFX double initialization problems
fork := true

// Add JavaFX dependencies
libraryDependencies ++= {
  // Determine OS version of JavaFX binaries
  lazy val osName = System.getProperty("os.name") match {
    case n if n.startsWith("Linux") => "linux"
    case n if n.startsWith("Mac") => "mac"
    case n if n.startsWith("Windows") => "win"
    case _ => throw new Exception("Unknown platform!")
  }
  Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
    .map(m => "org.openjfx" % s"javafx-$m" % "17.0.1" classifier osName)
}