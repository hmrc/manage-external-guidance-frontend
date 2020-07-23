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

package controllers.actions

import config.{AppConfig, UnauthorizedReviewErrorHandler}
import javax.inject.Inject
import models.requests.IdentifierRequest
import play.api.mvc.Results._
import play.api.mvc._
import play.api.{Configuration, Environment}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate

import scala.concurrent.ExecutionContext

trait TwoEyeReviewerIdentifierAction extends ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest]

class TwoEyeReviewerAuthenticatedIdentifierAction @Inject() (
    override val authConnector: AuthConnector,
    appConfig: AppConfig,
    override val parser: BodyParsers.Default,
    override val config: Configuration,
    override val env: Environment,
    unauthorizedReviewErrorHandler: UnauthorizedReviewErrorHandler
)(
    override implicit val executionContext: ExecutionContext
) extends AuthenticatedIdentifierBaseAction (authConnector, appConfig, parser, config, env) with TwoEyeReviewerIdentifierAction {

  override def accessRestrictions(): Predicate = Enrolment(appConfig.twoEyeReviewerRole)

  override def unauthorisedResult(request: Request[_]) : Result = Unauthorized(
    unauthorizedReviewErrorHandler.standardErrorTemplate(
      "error.unauthorized401.pageTitle.page",
      "error.unauthorized401.heading.page",
      "error.unauthorized401.message"
    )(request)
  )

}
