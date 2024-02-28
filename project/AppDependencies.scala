import sbt.*

object AppDependencies {

  val bootstrapVersion = "8.4.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-frontend-play-30" % bootstrapVersion,
    "uk.gov.hmrc" %% "play-frontend-hmrc-play-30" % "8.5.0"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalamock"                %% "scalamock"                % "5.2.0"   % "test",
    "uk.gov.hmrc"                  %% "bootstrap-test-play-30"   % bootstrapVersion  % "test"
  )

  val itDependencies: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-28"     % bootstrapVersion  % Test,
    "uk.gov.hmrc" %% "play-frontend-hmrc-play-30" % "8.5.0" % Test,
    "org.wiremock"       %  "wiremock"            % "3.4.2"  % Test
  )

}