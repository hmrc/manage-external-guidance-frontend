/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package config

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

trait AppConfig {
  val appName: String
  val analyticsToken: String
  val analyticsHost: String
  val reportAProblemPartialUrl: String
  val reportAProblemNonJSUrl: String
  val externalGuidanceBaseUrl: String
  val loginUrl: String
  val continueUrl: String
  val designerRole: String
  val twoEyeReviewerRole: String
  val factCheckerRole: String
  val publisherRole: String
  val gtmContainer: String
  val viewApprovalUrl: String
}

@Singleton
class AppConfigImpl @Inject() (config: Configuration, servicesConfig: ServicesConfig) extends AppConfig {
  private val contactBaseUrl = servicesConfig.baseUrl("contact-frontend")
  private val serviceIdentifier = "MyService"
  val analyticsToken: String = config.get[String](s"google-analytics.token")
  val analyticsHost: String = config.get[String](s"google-analytics.host")
  val reportAProblemPartialUrl: String = s"$contactBaseUrl/contact/problem_reports_ajax?service=$serviceIdentifier"
  val reportAProblemNonJSUrl: String = s"$contactBaseUrl/contact/problem_reports_nonjs?service=$serviceIdentifier"
  lazy val externalGuidanceBaseUrl: String = servicesConfig.baseUrl("external-guidance")
  lazy val viewExternalGuidanceBaseUrl: String = servicesConfig.baseUrl("view-external-guidance-frontend")
  val appName: String = config.get[String]("appName")
  lazy val loginUrl: String = servicesConfig.getString("strideAuth.login.url")
  lazy val continueUrl: String = servicesConfig.getString("strideAuth.login.continueUrl")
  lazy val designerRole: String = servicesConfig.getString("strideAuth.roles.designer")
  lazy val twoEyeReviewerRole: String = servicesConfig.getString("strideAuth.roles.twoEyeReviewer")
  lazy val factCheckerRole: String = servicesConfig.getString("strideAuth.roles.factChecker")
  lazy val publisherRole: String = servicesConfig.getString("strideAuth.roles.publisher")
  lazy val gtmContainer: String = config.get[String]("gtm.container")
  lazy val viewApprovalUrl: String = s"${viewExternalGuidanceBaseUrl}${config.get[String]("urls.viewExternalGuidanceApprovalUrl")}"

}
