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

import config.AppConfig
import javax.inject.Inject
import models.requests.IdentifierRequest
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc._
import play.api.{Configuration, Environment, Logger}
import uk.gov.hmrc.auth.core.AuthProvider.PrivilegedApplication
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Name, ~}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter
import uk.gov.hmrc.play.bootstrap.config.AuthRedirects

import scala.concurrent.{ExecutionContext, Future}

trait IdentifierActionBase extends ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest]

abstract class AuthenticatedIdentifierBaseAction @Inject()(
    override val authConnector: AuthConnector,
    appConfig: AppConfig,
    val parser: BodyParsers.Default,
    val config: Configuration,
    val env: Environment
)(
    implicit val executionContext: ExecutionContext
) extends IdentifierActionBase
    with AuthorisedFunctions
    with AuthRedirects {

  val logger: Logger = Logger(getClass)

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    val accessPredicate = AuthProviders(PrivilegedApplication) and accessRestrictions()
    // Allow access based on teh supplied Predicate
    authorised(accessPredicate)
      .retrieve(Retrievals.credentials and Retrievals.name and Retrievals.email and Retrievals.authorisedEnrolments) {
        case Some(Credentials(providerId, _)) ~ Some(Name(Some(name), _)) ~ Some(email) ~ authEnrolments =>
          println(s"********* $authEnrolments")
          println(s"********* ${Json.toJson(authEnrolments.enrolments)}")
          block(IdentifierRequest(request, providerId, name, email, authEnrolments.enrolments.map(_.key).toList))
        case _ =>
          logger.warn("Identifier action could not retrieve required user details in method invokeBlock")
          Future.successful(unauthorisedResult(request))
      } recover {
        case _: NoActiveSession =>
          Redirect(appConfig.loginUrl, Map("successURL" -> Seq(appConfig.continueUrl)))
        case authEx: AuthorisationException =>
          logger.warn(s"Method invokeBlock of identifier action received an authorization exception with the message ${authEx.getMessage}")
          unauthorisedResult(request)
      }
  }

  def accessRestrictions(): Predicate

  def unauthorisedResult(request: Request[_]) : Result

}
