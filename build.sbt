name := "langues"

version := "1.0"

lazy val `langues` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq( jdbc , anorm , cache , ws )

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"

libraryDependencies += "org.postgresql" % "postgresql" % "9.3-1100-jdbc4"

libraryDependencies += "org.squeryl" %% "squeryl" % "0.9.5-7"

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  
