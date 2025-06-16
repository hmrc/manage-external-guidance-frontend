import sbt.*

object AppDependencies {

  val bootstrapVersion = "9.13.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-frontend-play-30" % bootstrapVersion,
    "uk.gov.hmrc" %% "play-frontend-hmrc-play-30" % "12.6.0"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalamock"     %% "scalamock"              % "7.3.2",
    "uk.gov.hmrc"       %% "bootstrap-test-play-30" % bootstrapVersion,
    "org.scalatestplus" %% "mockito-5-12"           % "3.2.19.0",
    "org.mockito"       %  "mockito-core"           % "5.18.0"
  ).map(_ % Test)

  val itDependencies: Seq[ModuleID] = Seq()

}
