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

package controllers.actions

import config.{AppConfig, UnauthorizedReviewErrorHandler}
import javax.inject.Inject
import play.api.mvc._
import play.api.{Configuration, Environment}
import uk.gov.hmrc.auth.core.AuthProvider.PrivilegedApplication
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import scala.concurrent.ExecutionContext

trait DesignerAction extends IdentifierAction

class DesignerAuthenticatedAction @Inject() (
    override val authConnector: AuthConnector,
    val appConfig: AppConfig,
    val parser: BodyParsers.Default,
    val config: Configuration,
    val env: Environment,
    val errorHandler: UnauthorizedReviewErrorHandler
)(
    implicit val executionContext: ExecutionContext
) extends PrivilegedAction with DesignerAction {

  override val continueUrl: String = appConfig.designerAdminContinueUrl    

  val predicate: Predicate = Enrolment(appConfig.designerRole) and AuthProviders(PrivilegedApplication)
}
