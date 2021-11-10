import play.core.PlayVersion.current
import sbt._

object AppDependencies {
  val compile = Seq(
    "uk.gov.hmrc"             %% "play-language"              % "5.1.0-play-28",
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-28" % "5.16.0",
    "uk.gov.hmrc"             %% "play-frontend-hmrc"         % "1.24.0-play-28"
  )

  val test = Seq(
    "org.scalamock"                %% "scalamock"                % "5.1.0"   % "test",
    "org.jsoup"                    %  "jsoup"                    % "1.14.3"  % "test",
    "com.typesafe.play"            %% "play-test"                % current   % "test, it",
    "org.pegdown"                  %  "pegdown"                  % "1.6.0"   % "test, it",
    "com.github.tomakehurst"       %  "wiremock-jre8"            % "2.31.0"  % "test, it",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"     % "2.13.0"  % "test, it",
    "uk.gov.hmrc"                  %% "bootstrap-test-play-28"   % "5.16.0"  % "test, it"
  )

}
