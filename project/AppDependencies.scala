import sbt._

object AppDependencies {

  val bootstrapVersion = "9.0.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-frontend-play-30" % bootstrapVersion,
    "uk.gov.hmrc" %% "play-frontend-hmrc-play-30" % "10.2.0"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalamock"                %% "scalamock"                % "5.2.0"   % "test",
    "uk.gov.hmrc"                  %% "bootstrap-test-play-30"   % bootstrapVersion  % "test",
    "org.scalatestplus.play"        %% "scalatestplus-play"     % "7.0.1"          % "test",
    "org.scalatestplus"             %% "mockito-3-4"            % "3.2.10.0"       % "test",
    "org.mockito"                   % "mockito-core"            % "5.10.0"         % "test"
  )

  val itDependencies = Seq(
    "uk.gov.hmrc"                   %% "bootstrap-test-play-30" % bootstrapVersion  % Test,
  )

}
