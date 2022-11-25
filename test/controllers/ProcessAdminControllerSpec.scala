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

package controllers

import controllers.actions.FakeAuthorisedAction
import mocks.{MockAppConfig, MockProcessAdminService}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import views.html.published_summaries
import views.html.archived_summaries
import views.html.admin_signin
import play.api.libs.json._
import scala.concurrent.Future
import models.errors.{NotFoundError, InternalServerError}

class ProcessAdminControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite with MockProcessAdminService {

  private trait Test {

    private val pview = app.injector.instanceOf[published_summaries]
    private val aview = app.injector.instanceOf[archived_summaries]
    private val siview = app.injector.instanceOf[admin_signin]
    lazy val errorHandler = app.injector.instanceOf[config.ErrorHandler]
    implicit val hc: HeaderCarrier = HeaderCarrier()
    val fakeRequest = FakeRequest("GET", "/")
    val controller = new ProcessAdminController(
                            MockAppConfig,
                            FakeAuthorisedAction,
                            errorHandler,
                            pview,
                            aview,
                            siview,
                            mockProcessAdminService,
                            stubMessagesControllerComponents())

  }

  "GET /admin" should {
    "Redirect to list of published guidance" in new Test {
      val result = controller.admin(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
    }
  }

  "GET /admin/sign-in" should {

    "return 200" in new Test {

      val result = controller.signIn(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in new Test {
      val result = controller.signIn(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }
  }

  "POST /admin/sign-in" should {
    "Submit successful sign" in new Test {
      val fakePOSTRequest = FakeRequest("POST", "/external-guidance/admin/sign-in")
      val result = controller.submitSignIn(fakePOSTRequest.withFormUrlEncodedBody(("name", "admin"), ("password", "password")))

      status(result) shouldBe Status.SEE_OTHER
    }

    "Submit unsuccessful sign" in new Test {
      val fakePOSTRequest = FakeRequest("POST", "/external-guidance/admin/sign-in")
      val result = controller.submitSignIn(fakePOSTRequest.withFormUrlEncodedBody(("name", "BLAH"), ("password", "Blah")))

      status(result) shouldBe Status.UNAUTHORIZED
    }
  }


  "GET /admin/sign-out" should {

    "return 200" in new Test {

      val result = controller.signOut(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in new Test {
      val result = controller.signOut(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }

  }

  "GET /admin/published" should {

    "return 200" in new Test {

      MockProcessAdminService.publishedSummaries.returns(Future.successful(Right(List())))

      val result = controller.listPublished(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in new Test {

      MockProcessAdminService.publishedSummaries.returns(Future.successful(Right(List())))

      val result = controller.listPublished(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }

    "Return Internal server error when retrieval fails" in new Test {
      MockProcessAdminService.publishedSummaries.returns(Future.successful(Left(InternalServerError)))

      val result = controller.listPublished(fakeRequest)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
    }
  }

  "GET /admin/published/code" should {

    "return 200" in new Test {

      MockProcessAdminService.getPublishedByProcessCode("code").returns(Future.successful(Right(Json.obj())))

      val result = controller.getPublished("code")(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return Json" in new Test {

      MockProcessAdminService.getPublishedByProcessCode("code").returns(Future.successful(Right(Json.obj())))

      val result = controller.getPublished("code")(fakeRequest)
      contentType(result) shouldBe Some("application/json")
    }

    "Return Bad request when retrieval fails" in new Test {
      MockProcessAdminService.getPublishedByProcessCode("unknown").returns(Future.successful(Left(NotFoundError)))

      val result = controller.getPublished("unknown")(fakeRequest)
      status(result) shouldBe Status.BAD_REQUEST
    }

  }

  "GET /admin/archived" should {

    "return 200" in new Test {

      MockProcessAdminService.archivedSummaries.returns(Future.successful(Right(List())))

      val result = controller.listArchived(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in new Test {

      MockProcessAdminService.archivedSummaries.returns(Future.successful(Right(List())))

      val result = controller.listArchived(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }

    "Return Internal server error when retrieval fails" in new Test {
      MockProcessAdminService.archivedSummaries.returns(Future.successful(Left(InternalServerError)))

      val result = controller.listArchived(fakeRequest)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
    }

  }

  "GET /admin/archived/id" should {

    "return 200" in new Test {

      MockProcessAdminService.getArchivedById("id").returns(Future.successful(Right(Json.obj())))

      val result = controller.getArchived("id")(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return Json" in new Test {

      MockProcessAdminService.getArchivedById("id").returns(Future.successful(Right(Json.obj())))

      val result = controller.getArchived("id")(fakeRequest)
      contentType(result) shouldBe Some("application/json")
    }

    "Return Bad request when retrieval fails" in new Test {
      MockProcessAdminService.getArchivedById("unknown").returns(Future.successful(Left(NotFoundError)))

      val result = controller.getArchived("unknown")(fakeRequest)
      status(result) shouldBe Status.BAD_REQUEST
    }

  }

}
