import play.core.PlayVersion.current
import sbt._

object AppDependencies {
  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-frontend-play-28" % "7.21.0",
    "uk.gov.hmrc" %% "play-frontend-hmrc"         % "7.17.0-play-28"
  )

  val test = Seq(
    "org.scalamock"                %% "scalamock"                % "5.2.0"   % "test",
    "org.jsoup"                    %  "jsoup"                    % "1.16.1"  % "test",
    "com.typesafe.play"            %% "play-test"                % current   % "test, it",
    "com.github.tomakehurst"       %  "wiremock-jre8"            % "2.35.0"  % "test, it",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"     % "2.15.2"  % "test, it",
    "uk.gov.hmrc"                  %% "bootstrap-test-play-28"   % "7.21.0"  % "test, it"
  )

}
