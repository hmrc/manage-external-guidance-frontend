/*
 * Copyright 2023 HM Revenue & Customs
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
  val timescalesContinueUrl: String
  val processAdminContinueUrl: String
  val designerRole: String
  val twoEyeReviewerRole: String
  val factCheckerRole: String
  val gtmContainer: String
  val viewApprovalUrl: String
  val pageMapApprovalUrl: String
  val pageMapPublishedlUrl: String
  val activeProcessesUrl: String
  val commentsAndFeedbackUrl: String
  val processAdminUser: String
  val processAdminPassword: String
}

@Singleton
class AppConfigImpl @Inject() (config: Configuration, servicesConfig: ServicesConfig) extends AppConfig {

  private lazy val host = servicesConfig.getString("host")
  private lazy val contactBaseUrl = servicesConfig.baseUrl("contact-frontend")
  private lazy val serviceIdentifier = servicesConfig.getString("contact-frontend-urls.serviceIdentifier")
  private lazy val externalGuidanceViewerHost: String = config.get[String]("external-guidance-viewer.host")
  private lazy val externalGuidanceViewerApiHost: String = config.get[String]("external-guidance-viewer.api-host")

  val analyticsToken: String = config.get[String](s"google-analytics.token")
  val analyticsHost: String = config.get[String](s"google-analytics.host")
  val reportAProblemPartialUrl: String = s"$contactBaseUrl/contact/problem_reports_ajax?service=$serviceIdentifier"
  val reportAProblemNonJSUrl: String = s"""$contactBaseUrl${servicesConfig.getString("contact-frontend-urls.reportAProblemNonJSUrl")}"""
  lazy val externalGuidanceBaseUrl: String = servicesConfig.baseUrl("external-guidance")
  val appName: String = config.get[String]("appName")
  val commentsAndFeedbackUrl = config.get[String]("appLinks.commentsAndFeedbackUrl")
  lazy val loginUrl: String = servicesConfig.getString("strideAuth.login.url")
  lazy val continueUrl: String = host + servicesConfig.getString("strideAuth.login.continueUrl")
  lazy val timescalesContinueUrl: String = host + servicesConfig.getString("strideAuth.login.timescalesContinueUrl")
  lazy val processAdminContinueUrl: String = host + servicesConfig.getString("strideAuth.login.processAdminContinueUrl")
  lazy val designerRole: String = servicesConfig.getString("strideAuth.roles.designer")
  lazy val twoEyeReviewerRole: String = servicesConfig.getString("strideAuth.roles.twoEyeReviewer")
  lazy val factCheckerRole: String = servicesConfig.getString("strideAuth.roles.factChecker")
  lazy val gtmContainer: String = config.get[String]("gtm.container")
  lazy val viewApprovalUrl: String = s"$externalGuidanceViewerHost${config.get[String]("external-guidance-viewer.approvalUrl")}"
  lazy val pageMapApprovalUrl: String = s"$externalGuidanceViewerHost${config.get[String]("external-guidance-viewer.pageMapApprovalUrl")}"
  lazy val pageMapPublishedlUrl: String = s"$externalGuidanceViewerHost${config.get[String]("external-guidance-viewer.pageMapPublishedUrl")}"
  lazy val activeProcessesUrl: String = s"$externalGuidanceViewerApiHost${config.get[String]("external-guidance-viewer.activeProcessesUrl")}"
  lazy val processAdminUser: String = config.get[String]("admin-username")
  lazy val processAdminPassword: String = config.get[String]("admin-password")
}
