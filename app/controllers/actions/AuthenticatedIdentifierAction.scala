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
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Name, ~}
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter
import uk.gov.hmrc.play.bootstrap.config.AuthRedirects

import scala.concurrent.{ExecutionContext, Future}

trait IdentifierAction extends ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest]

class AuthenticatedIdentifierAction @Inject() (
    override val authConnector: AuthConnector,
    appConfig: AppConfig,
    val parser: BodyParsers.Default,
    val config: Configuration,
    val env: Environment,
    errorHandler: ErrorHandler
)(
    implicit val executionContext: ExecutionContext
) extends IdentifierAction
    with AuthorisedFunctions
    with AuthRedirects {

  val logger: Logger = Logger(getClass)

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    val unauthorizedResult = Unauthorized(
      errorHandler.standardErrorTemplate(
        "error.unauthorized401.pageTitle.service",
        "error.unauthorized401.heading.service",
        "error.unauthorized401.message"
      )(request)
    )

    val allUsersPredicate: Predicate = AuthProviders(PrivilegedApplication) and
      (
        Enrolment(appConfig.designerRole) or
          Enrolment(appConfig.twoEyeReviewerRole) or
          Enrolment(appConfig.factCheckerRole) or
          Enrolment(appConfig.publisherRole)
      )

    // Allow access for all roles
    authorised(allUsersPredicate).retrieve(Retrievals.credentials and Retrievals.name and Retrievals.email) {
      case Some(Credentials(providerId, _)) ~ Some(Name(Some(name), _)) ~ Some(email) =>
        block(IdentifierRequest(request, providerId, name, email))
      case _ =>
        logger.warn("Identifier action could not retrieve required user details in method invokeBlock")
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
