name := """howtoeat"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaCore,
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "mysql" % "mysql-connector-java" % "5.1.18"
)


javaOptions in Test ++= Seq(
  "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=9998",
  "-Xms512M",
  "-Xmx1536M",
  "-Xss1M",
  "-XX:MaxPermSize=384M"
)

val appDependencies = Seq(
  "be.objectify"  %% "deadbolt-java"     % "2.3.2",
  // Comment the next line for local development of the Play Authentication core:
  "com.feth"      %% "play-authenticate" % "0.6.8",
  "org.webjars" %% "webjars-play" % "2.3.0",
  "org.webjars" % "bootstrap" % "3.2.0"
)

libraryDependencies ++= appDependencies

val buildSettings = Defaults.defaultSettings ++ Seq(
  javaOptions += "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=9998",
  javaOptions += "-Xms512M",
  javaOptions += "-Xmx1G",
  javaOptions += "-XX:MaxPermSize=384M"
)