import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val bootstrapVersion = "8.2.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-frontend-play-28" % bootstrapVersion,
    "uk.gov.hmrc" %% "play-frontend-hmrc-play-28" % "8.5.0"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalamock"                %% "scalamock"                % "5.2.0"   % "test",
    "org.jsoup"                    %  "jsoup"                    % "1.17.1"  % "test",
    "com.typesafe.play"            %% "play-test"                % current   % "test",
    "com.github.tomakehurst"       %  "wiremock-jre8"            % "2.35.1"  % "test",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"     % "2.16.0"  % "test",
    "uk.gov.hmrc"                  %% "bootstrap-test-play-28"   % bootstrapVersion  % "test"
  )

  val itDependencies: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                   %% "bootstrap-test-play-28" % bootstrapVersion  % Test,
    "com.github.tomakehurst"        % "wiremock-jre8"           % "2.35.1"          % Test,
    "com.fasterxml.jackson.module"  %% "jackson-module-scala"   % "2.16.0"          % Test,
    "uk.gov.hmrc"                   %% "bootstrap-test-play-28" % bootstrapVersion  % Test
  )

}
