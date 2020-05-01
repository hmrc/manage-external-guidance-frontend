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

import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}
import play.api.http.Status
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.{AuthConnector, AuthProviders, AuthorisationException, Enrolment}
import uk.gov.hmrc.auth.core.AuthProvider.PrivilegedApplication
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Retrieval}
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals._
import uk.gov.hmrc.http.HeaderCarrier
import controllers.routes
import base.AuthBaseSpec
import mocks.{MockAppConfig, MockAuthConnector}
import play.api.{Configuration, Environment}

class AuthenticatedIdentifierActionSpec extends AuthBaseSpec with MockAuthConnector {

  // Define simple harness class to represent controller
  class Harness(authenticatedIdentifierAction: IdentifierAction) {

    def onPageLoad(): Action[AnyContent] = authenticatedIdentifierAction { _ =>
      Results.Ok
    }
  }

  // Define fake authentication connector to simulate authentication failures
  class FakeFailingAuthConnector @Inject() (exceptionToBeReturned: Throwable) extends AuthConnector {

    override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] =
      Future.failed(exceptionToBeReturned)
  }

  trait AuthTestData {

    val path: String = "/path"
    val fakeRequest = FakeRequest("GET", path)

    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

    val bodyParser = app.injector.instanceOf[BodyParsers.Default]
    val config = app.injector.instanceOf[Configuration]
    val env = app.injector.instanceOf[Environment]
  }

  "AuthenticatedIdentifierAction" should {

    "Enable authorized users to access the functionality of a controller" in new AuthTestData {

      MockAuthConnector
        .authorize(
          (Enrolment(MockAppConfig.designerRole) or
            Enrolment(MockAppConfig.approverRole) or
            Enrolment(MockAppConfig.publisherRole) and
            AuthProviders(PrivilegedApplication)),
          credentials
        )
        .returns(Future.successful(Some(Credentials("id", "type"))))

      val authAction: AuthenticatedIdentifierAction = new AuthenticatedIdentifierAction(mockAuthConnector, MockAppConfig, bodyParser, config, env)

      val target: Harness = new Harness(authAction)

      val result = target.onPageLoad()(fakeRequest)

      status(result) shouldBe Status.OK
    }

    "Redirect user to unauthorized page if authentication connector fails to return provider details" in new AuthTestData {

      MockAuthConnector
        .authorize(
          (Enrolment(MockAppConfig.designerRole) or
            Enrolment(MockAppConfig.approverRole) or
            Enrolment(MockAppConfig.publisherRole) and
            AuthProviders(PrivilegedApplication)),
          credentials
        )
        .returns(Future.successful(None))

      val authAction: AuthenticatedIdentifierAction = new AuthenticatedIdentifierAction(mockAuthConnector, MockAppConfig, bodyParser, config, env)

      val target: Harness = new Harness(authAction)

      val result = target.onPageLoad()(fakeRequest)

      status(result) shouldBe SEE_OTHER

      redirectLocation(result) shouldBe Some(routes.UnauthorizedController.onPageLoad().url)
    }
  }

  "Redirect user to Stride login of no session record exists" in new AuthTestData {

    val authAction: AuthenticatedIdentifierAction =
      new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(AuthorisationException.fromString("SessionRecordNotFound")), MockAppConfig, bodyParser, config, env)

    val target: Harness = new Harness(authAction)

    val result = target.onPageLoad()(fakeRequest)

    status(result) shouldBe SEE_OTHER

    redirectLocation(result) match {
      case Some(url) => url should include("/stride/sign-in")
      case _ => fail("Stride login redirect location is incorrect")
    }
  }

  "Redirect user to unauthorised page if authentication fails" in new AuthTestData {

    val authAction: AuthenticatedIdentifierAction =
      new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(AuthorisationException.fromString("Insufficient Enrolments")), MockAppConfig, bodyParser, config, env)

    val target: Harness = new Harness(authAction)

    val result = target.onPageLoad()(fakeRequest)

    status(result) shouldBe SEE_OTHER
  }

}
