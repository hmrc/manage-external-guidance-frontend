/*
 * Copyright 2021 HM Revenue & Customs
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
import play.api.mvc.RequestHeader
import uk.gov.hmrc.play.bootstrap.binders.SafeRedirectUrl
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
  val gtmContainer: String
  val viewApprovalUrl: String
  val commentsAndFeedbackUrl: String

  def contactFrontendFeedbackUrl(implicit requestHeader: RequestHeader): String
}

@Singleton
class AppConfigImpl @Inject() (config: Configuration, servicesConfig: ServicesConfig) extends AppConfig {

  private lazy val host = servicesConfig.getString("host")
  private lazy val contactBaseUrl = servicesConfig.baseUrl("contact-frontend")
  private lazy val serviceIdentifier = servicesConfig.getString("contact-frontend-urls.serviceIdentifier")
  private lazy val betaFeedback = servicesConfig.getString("contact-frontend-urls.betaFeedback")
  private lazy val externalGuidanceViewerHost: String = config.get[String]("external-guidance-viewer.host")

  val analyticsToken: String = config.get[String](s"google-analytics.token")
  val analyticsHost: String = config.get[String](s"google-analytics.host")
  val reportAProblemPartialUrl: String = s"$contactBaseUrl/contact/problem_reports_ajax?service=$serviceIdentifier"
  val reportAProblemNonJSUrl: String = s"""$contactBaseUrl${servicesConfig.getString("contact-frontend-urls.reportAProblemNonJSUrl")}"""
  lazy val externalGuidanceBaseUrl: String = servicesConfig.baseUrl("external-guidance")
  val appName: String = config.get[String]("appName")
  val commentsAndFeedbackUrl = config.get[String]("appLinks.commentsAndFeedbackUrl")
  lazy val loginUrl: String = servicesConfig.getString("strideAuth.login.url")
  lazy val continueUrl: String = servicesConfig.getString("strideAuth.login.continueUrl")
  lazy val designerRole: String = servicesConfig.getString("strideAuth.roles.designer")
  lazy val twoEyeReviewerRole: String = servicesConfig.getString("strideAuth.roles.twoEyeReviewer")
  lazy val factCheckerRole: String = servicesConfig.getString("strideAuth.roles.factChecker")
  lazy val gtmContainer: String = config.get[String]("gtm.container")
  lazy val viewApprovalUrl: String = s"$externalGuidanceViewerHost${config.get[String]("external-guidance-viewer.approvalUrl")}"

  def contactFrontendFeedbackUrl(implicit requestHeader: RequestHeader): String = {
    s"$contactBaseUrl$betaFeedback?service=$serviceIdentifier&backUrl=${SafeRedirectUrl(host + requestHeader.uri).encodedUrl}"
  }

}
