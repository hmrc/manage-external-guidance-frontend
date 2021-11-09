import play.core.PlayVersion.current
import sbt._

object AppDependencies {
  val compile = Seq(
    "uk.gov.hmrc"             %% "play-language"            % "5.1.0-play-28",
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-28" % "5.16.0",
    "uk.gov.hmrc"             %% "play-frontend-hmrc"       % "1.18.0-play-28"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"   % "5.16.0"               % "test, it",
    "org.scalamock"           %% "scalamock"                % "5.1.0"                 % "test",
    "org.jsoup"               %  "jsoup"                    % "1.14.1"                % "test",
    "com.typesafe.play"       %% "play-test"                % current                 % "test, it",
    "org.pegdown"             %  "pegdown"                  % "1.6.0"                 % "test, it",
    "com.github.tomakehurst"  % "wiremock"                  % "2.27.2"                % "test, it",
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.12.5"               % "test, it"
  )

  // Fixes a transitive dependency clash between wiremock and scalatestplus-play
  val overrides: Seq[ModuleID] = {
    val jettyFromWiremockVersion = "9.4.44.v20210927"
    Seq(
      "org.eclipse.jetty" % "jetty-client" % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-continuation" % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-http" % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-io" % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-security" % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-server" % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-servlet" % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-servlets" % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-util" % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-webapp" % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-xml" % jettyFromWiremockVersion,
      "org.eclipse.jetty.websocket" % "websocket-api" % jettyFromWiremockVersion,
      "org.eclipse.jetty.websocket" % "websocket-client" % jettyFromWiremockVersion,
      "org.eclipse.jetty.websocket" % "websocket-common" % jettyFromWiremockVersion
    )
  }
}
