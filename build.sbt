import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

val appName = "ddcops-user-managment-frontend-poc-scala"

val silencerVersion = "1.7.3"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin, DockerPlugin)
  .settings(
    majorVersion := 0,
    scalaVersion := "2.12.13",
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    pipelineStages in Assets := Seq(gzip),
    // ***************
    // Use the silencer plugin to suppress warnings
    scalacOptions += "-P:silencer:pathFilters=routes",
    libraryDependencies ++= Seq(
      compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
      "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
    )
    // ***************
  )
  .settings(publishingSettings: _*)
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(
    dockerExposedPorts := Seq(9000),
    javaOptions in Universal ++= Seq(
      "-J-Xmx1024m",
      "-J-Xms512m",
      // don't write any pid files
      "-Dpidfile.path=/dev/null",
      // reference a logback config file that has no file appenders
      "-Dlogback.configurationFile=logback-prod.xml"
    )
  )
