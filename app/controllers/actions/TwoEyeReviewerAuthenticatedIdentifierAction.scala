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

import config.{AppConfig, ErrorHandler}
import javax.inject.Inject
import models.requests.IdentifierRequest
import play.api.mvc.Results._
import play.api.mvc._
import play.api.{Configuration, Environment, Logger}
import uk.gov.hmrc.auth.core.AuthProvider.PrivilegedApplication
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.Credentials
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter
import uk.gov.hmrc.play.bootstrap.config.AuthRedirects

import scala.concurrent.{ExecutionContext, Future}

trait TwoEyeReviewerIdentifierAction extends ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest]

class TwoEyeReviewerAuthenticatedIdentifierAction @Inject() (
    override val authConnector: AuthConnector,
    appConfig: AppConfig,
    val parser: BodyParsers.Default,
    val config: Configuration,
    val env: Environment,
    errorHandler: ErrorHandler
)(
    implicit val executionContext: ExecutionContext
) extends TwoEyeReviewerIdentifierAction
    with AuthorisedFunctions
    with AuthRedirects {

  val logger: Logger = Logger(getClass)

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    val unauthorizedResult = Unauthorized(
      errorHandler.standardErrorTemplate(
        "error.unauthorized401.pageTitle",
        "error.unauthorized401.heading",
        "error.unauthorized401.message"
      )(request)
    )

    // Restrict access to users with 2i reviewer role
    authorised(Enrolment(appConfig.twoEyeReviewerRole) and AuthProviders(PrivilegedApplication)).retrieve(credentials) {
      case Some(Credentials(providerId, _)) =>
        block(IdentifierRequest(request, providerId))
      case None =>
        logger.warn("Identifier action could not retrieve provider identifier in method invokeBlock")
        Future.successful(unauthorizedResult)
    } recover {
      case _: NoActiveSession =>
        Redirect(appConfig.loginUrl, Map("successURL" -> Seq(appConfig.continueUrl)))
      case authEx: AuthorisationException =>
        logger.warn(s"Method invokeBlock of identifier action received an authorization exception with the message ${authEx.getMessage}")
        unauthorizedResult
    }
  }
}