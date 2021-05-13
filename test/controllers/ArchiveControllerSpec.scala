/*
 * Copyright 2021 HM Revenue & Customs
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

import config.ErrorHandler
import controllers.actions.FakeIdentifierAction
import forms.UnpublishConfirmationFormProvider
import mocks.MockArchiveConnector
import models.PublishedProcess
import models.errors.InternalServerError
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import views.html.{unpublish_confirmation, unpublished}

import java.time.ZonedDateTime
import scala.concurrent.Future

class ArchiveControllerSpec extends WordSpec with Matchers with GuiceOneAppPerSuite with MockArchiveConnector {

  private trait Test {
    private val view = app.injector.instanceOf[unpublish_confirmation]
    private val confirmation = app.injector.instanceOf[unpublished]
    lazy val errorHandler: ErrorHandler = app.injector.instanceOf[config.ErrorHandler]
    implicit val hc: HeaderCarrier = HeaderCarrier()
    val formProvider = new UnpublishConfirmationFormProvider()
    val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")
    val controller = new ArchiveController(
      errorHandler,
      FakeIdentifierAction,
      mockArchiveConnector,
      view,
      confirmation,
      formProvider,
      stubMessagesControllerComponents()
    )
  }

  "GET /unpublish/id" should {

    val published = PublishedProcess(
      "id",
      1,
      ZonedDateTime.now(),
      Json.obj("a" -> "b"),
      "user",
      "code"
    )

    "return 200" in new Test {
      MockArchiveConnector.getPublished("id")
        .returns(Future.successful(Right(published)))

      val result: Future[Result] = controller.unpublish("id")(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return 400" in new Test {
      MockArchiveConnector.getPublished("id")
        .returns(Future.successful(Left(InternalServerError)))

      val result: Future[Result] = controller.unpublish("id")(fakeRequest)
      status(result) shouldBe Status.BAD_REQUEST
    }
  }

  "GET /archive/id" should {

    "return 200 when archiving process is successful" in new Test {
      MockArchiveConnector.archive("id")
        .returns(Future.successful(Right(true)))

      val result: Future[Result] = controller.archive("id", "process")(fakeRequest.withFormUrlEncodedBody(("value", "Yes")))
      status(result) shouldBe Status.OK
    }

    "return 303 when leaving process published" in new Test {
      val result: Future[Result] = controller.archive("id", "process")(fakeRequest.withFormUrlEncodedBody(("value", "No")))
      status(result) shouldBe Status.SEE_OTHER
    }

    "return 400 when archiving process failed" in new Test {
      MockArchiveConnector.archive("id")
        .returns(Future.successful(Left(InternalServerError)))

      val result: Future[Result] = controller.archive("id", "process")(fakeRequest.withFormUrlEncodedBody(("value", "Yes")))
      status(result) shouldBe Status.BAD_REQUEST
    }

    "return 400 if no answer is provided" in new Test {
      val result: Future[Result] = controller.archive("id", "process")(fakeRequest)
      status(result) shouldBe Status.BAD_REQUEST
    }
  }

}
