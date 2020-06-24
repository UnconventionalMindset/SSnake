organization := "io.github.jacopogobbi"

name := "SSnake"

version := "0.1"

scalaVersion := "2.13.2"

// Determine OS version of JavaFX binaries
lazy val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux") => "linux"
  case n if n.startsWith("Mac") => "mac"
  case n if n.startsWith("Windows") => "win"
  case _ => throw new Exception("Unknown platform!")
}

lazy val javaFXModules = Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
libraryDependencies ++=
  // Add dependency on JavaFX libraries, OS dependent
  javaFXModules.map(m => "org.openjfx" % s"javafx-$m" % "14.0.1" classifier osName) ++
    Seq(
      // Add dependency on ScalaFX library
      "org.scalafx" %% "scalafx" % "14-R19",
      "com.beachape" %% "enumeratum" % "1.6.1"
    )
