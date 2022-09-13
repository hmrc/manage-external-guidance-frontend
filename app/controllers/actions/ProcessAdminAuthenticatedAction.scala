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

package controllers.actions

import config.{AppConfig, ErrorHandler}
import javax.inject.Inject
import play.api.mvc._
import play.api.{Configuration, Environment}
import uk.gov.hmrc.auth.core.AuthProvider.PrivilegedApplication
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate

import scala.concurrent.ExecutionContext

trait ProcessAdminAction extends IdentifierAction

class ProcessAdminAuthenticatedAction @Inject() (
    override val authConnector: AuthConnector,
    val appConfig: AppConfig,
    val parser: BodyParsers.Default,
    val config: Configuration,
    val env: Environment,
    val errorHandler: ErrorHandler
)(
    implicit val executionContext: ExecutionContext
) extends PrivilegedAction with ProcessAdminAction {

  override val continueUrl: String = appConfig.processAdminContinueUrl

  val predicate: Predicate = AuthProviders(PrivilegedApplication) and
                             (Enrolment(appConfig.designerRole) or
                              Enrolment(appConfig.twoEyeReviewerRole) or
                              Enrolment(appConfig.factCheckerRole))
}
