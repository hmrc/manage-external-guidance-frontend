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

import base.ControllerBaseSpec
import config.ErrorHandler
import mocks.{MockAppConfig, MockAuthConnector}
import play.api.http.Status
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.auth.core.AuthorisationException
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Name, ~}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class AllRolesAuthenticatedActionSpec extends ControllerBaseSpec with MockAuthConnector {

  // Define simple harness class to represent controller
  class Harness(authenticatedIdentifierAction: AllRolesAction) {

    def onPageLoad(): Action[AnyContent] = authenticatedIdentifierAction { _ =>
      Results.Ok
    }
  }

  trait AuthTestData {

    val path: String = "/path"
    val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", path)

    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

    lazy val mockErrorHandler: ErrorHandler = mock[ErrorHandler]
    (mockErrorHandler
      .standardErrorTemplate(_: String, _: String, _: String)(_: Request[_]))
      .stubs(*, *, *, *)
      .returns(Future.successful(Html("")))

    lazy val authAction = new AllRolesAuthenticatedAction(mockAuthConnector, MockAppConfig, bodyParser, config, env, mockErrorHandler)

    lazy val target = new Harness(authAction)
  }

  "AllRolesAuthenticatedAction" should {

    "grant access if authorisation is successful" in new AuthTestData {

      val authResult = new ~(new ~(Some(Credentials("id", "type")), Some(Name(Some("name"), None))), Some("email"))

      MockAuthConnector.authorize().returns(Future.successful(authResult))

      val result: Future[Result] = target.onPageLoad()(fakeRequest)

      status(result) shouldBe Status.OK
    }

    "deny access to user if no credentials returned" in new AuthTestData {

      val authResult = new ~(new ~(None, Some(Name(Some("name"), None))), Some("email"))

      MockAuthConnector.authorize().returns(Future.successful(authResult))

      val result: Future[Result] = target.onPageLoad()(fakeRequest)

      status(result) shouldBe UNAUTHORIZED
    }

    "deny access to user if no name instance returned" in new AuthTestData {

      val authResult = new ~(new ~(Some(Credentials("id", "type")), None), Some("email"))

      MockAuthConnector.authorize().returns(Future.successful(authResult))

      val result: Future[Result] = target.onPageLoad()(fakeRequest)

      status(result) shouldBe UNAUTHORIZED
    }

    "deny access to user if no name detail returned" in new AuthTestData {

      val authResult = new ~(new ~(Some(Credentials("id", "type")), Some(Name(None, None))), Some("email"))

      MockAuthConnector.authorize().returns(Future.successful(authResult))

      val result: Future[Result] = target.onPageLoad()(fakeRequest)

      status(result) shouldBe UNAUTHORIZED
    }

    "deny access to user if no email address returned" in new AuthTestData {

      val authResult = new ~(new ~(Some(Credentials("id", "type")), Some(Name(Some("name"), None))), None)

      MockAuthConnector.authorize().returns(Future.successful(authResult))

      val result: Future[Result] = target.onPageLoad()(fakeRequest)

      status(result) shouldBe UNAUTHORIZED
    }

    "deny access to user if no defined details returned" in new AuthTestData {

      val authResult = new ~(new ~(None, None), None)

      MockAuthConnector.authorize().returns(Future.successful(authResult))

      val result: Future[Result] = target.onPageLoad()(fakeRequest)

      status(result) shouldBe UNAUTHORIZED
    }

    "redirect user to Stride login if no session record exists" in new AuthTestData {

      val error: Throwable = AuthorisationException.fromString("SessionRecordNotFound")
      MockAuthConnector.authorize().returns(Future.failed(error))

      val result: Future[Result] = target.onPageLoad()(fakeRequest)

      status(result) shouldBe SEE_OTHER

      redirectLocation(result) match {
        case Some(url) => url should include("/stride/sign-in")
        case _ => fail("Stride login redirect location is incorrect")
      }
    }

    "deny access to user if authorisation fails" in new AuthTestData {

      val error: Throwable = AuthorisationException.fromString("InsufficientEnrolments")
      MockAuthConnector.authorize().returns(Future.failed(error))

      val result: Future[Result] = target.onPageLoad()(fakeRequest)

      status(result) shouldBe UNAUTHORIZED
    }
  }
}
