/*
 * Copyright 2022 HM Revenue & Customs
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

package mocks

import config.AppConfig
import play.api.mvc.RequestHeader

object MockAppConfig extends AppConfig {
  override val analyticsToken: String = "token"
  override val analyticsHost: String = "host"
  override val reportAProblemPartialUrl: String = "someUrl"
  override val reportAProblemNonJSUrl: String = "someJsUrl"
  override val externalGuidanceBaseUrl: String = "http://external-guidance-base-url"
  override val appName: String = "manage-external-guidance-frontend"
  lazy val processAdminContinueUrl: String = "http://localhost:9740/external-guidance/admin/published"
  lazy val loginUrl: String = "http://localhost:9041/stride/sign-in"
  lazy val continueUrl: String = "http://localhost:9740/external-guidance/hello-world"
  lazy val designerRole: String = "Designer"
  lazy val twoEyeReviewerRole: String = "2iReviewer"
  lazy val factCheckerRole: String = "FactChecker"
  override lazy val gtmContainer: String = "someContainer"
  lazy val viewApprovalUrl: String = "http://localhost:9741/guidance/approval"
  lazy val commentsAndFeedbackUrl: String = "http://www.gov.uk"
  lazy val timescalesContinueUrl: String = "http://localhost:9740/external-guidance/timescales"

  override def contactFrontendFeedbackUrl(implicit request: RequestHeader): String = "somefeedbackUrl"
}
