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

package controllers

import base.ControllerBaseSpec
import config.ErrorHandler
import controllers.actions.FakeTwoEyeReviewerIdentifierAction
import forms.TwoEyeReviewResultFormProvider
import mocks.MockReviewService
import models.errors.{IncompleteDataError, InternalServerError, MalformedResponseError, NotFoundError, StaleDataError}
import models.{ApprovalStatus, ReviewData}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.Form
import play.api.http.{MimeTypes, Status}
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import views.html.{twoeye_confirm_error, twoeye_review_result}

import scala.concurrent.Future

class TwoEyeReviewResultControllerSpec extends ControllerBaseSpec with GuiceOneAppPerSuite with MockReviewService {

  private trait Test extends ReviewData {

    implicit val hc: HeaderCarrier = HeaderCarrier()

    val errorHandler: ErrorHandler = injector.instanceOf[ErrorHandler]

    val view: twoeye_review_result = injector.instanceOf[twoeye_review_result]
    val errorView: twoeye_confirm_error = injector.instanceOf[twoeye_confirm_error]
    val formProvider: TwoEyeReviewResultFormProvider = new TwoEyeReviewResultFormProvider()
    val form: Form[ApprovalStatus] = formProvider()

    def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

    implicit val messages: Messages = messagesApi.preferred(FakeRequest("GET", "/"))

    val reviewController: TwoEyeReviewResultController =
      new TwoEyeReviewResultController(errorHandler, FakeTwoEyeReviewerIdentifierAction, formProvider, view, errorView, mockReviewService, messagesControllerComponents)

    val fakePostRequest: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest("POST", "/").withFormUrlEncodedBody(("value", ApprovalStatus.WithDesignerForUpdate.toString))
    val fakeGetRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")
  }

  "The two eye review controller" should {

    "Return SEE_OTHER for a successful post of the review completion for a process" in new Test {

      MockReviewService
        .approval2iReviewComplete(id, credential, name, ApprovalStatus.WithDesignerForUpdate)
        .returns(Future.successful(Right(())))

      val result: Future[Result] = reviewController.onSubmit(id)(fakePostRequest)

      status(result) shouldBe Status.SEE_OTHER
    }

    "Return No content for a successful post" in new Test {

      MockReviewService
        .approval2iReviewComplete(id, credential, name, ApprovalStatus.WithDesignerForUpdate)
        .returns(Future.successful(Right(())))

      val result: Future[Result] = reviewController.onSubmit(id)(fakePostRequest)

      contentType(result) shouldBe None
    }

    "Return the Http status Not found when the process review does not exist" in new Test {

      MockReviewService
        .approval2iReviewComplete(id, credential, name, ApprovalStatus.WithDesignerForUpdate)
        .returns(Future.successful(Left(NotFoundError)))

      val result: Future[Result] = reviewController.onSubmit(id)(fakePostRequest)

      status(result) shouldBe Status.NOT_FOUND
    }

    "Return an Html error page when the process review does not exist" in new Test {

      MockReviewService
        .approval2iReviewComplete(id, credential, name, ApprovalStatus.WithDesignerForUpdate)
        .returns(Future.successful(Left(NotFoundError)))

      val result: Future[Result] = reviewController.onSubmit(id)(fakePostRequest)

      contentType(result) shouldBe Some(MimeTypes.HTML)
    }

    "Return the Http status internal server error when the process review data returned to the application is malformed" in new Test {

      MockReviewService
        .approval2iReviewComplete(id, credential, name, ApprovalStatus.WithDesignerForUpdate)
        .returns(Future.successful(Left(MalformedResponseError)))

      val result: Future[Result] = reviewController.onSubmit(id)(fakePostRequest)

      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
    }

    "Return an Html error page when the process review data returned to the application is malformed" in new Test {

      MockReviewService
        .approval2iReviewComplete(id, credential, name, ApprovalStatus.WithDesignerForUpdate)
        .returns(Future.successful(Left(MalformedResponseError)))

      val result: Future[Result] = reviewController.onSubmit(id)(fakePostRequest)

      contentType(result) shouldBe Some(MimeTypes.HTML)
    }

    "Return the Http status not found when the process review data requested is out of date in some way" in new Test {

      MockReviewService
        .approval2iReviewComplete(id, credential, name, ApprovalStatus.WithDesignerForUpdate)
        .returns(Future.successful(Left(StaleDataError)))

      val result: Future[Result] = reviewController.onSubmit(id)(fakePostRequest)

      status(result) shouldBe Status.NOT_FOUND
    }

    "Return an Html error page when the process review data requested is out of date in some way" in new Test {

      MockReviewService
        .approval2iReviewComplete(id, credential, name, ApprovalStatus.WithDesignerForUpdate)
        .returns(Future.successful(Left(StaleDataError)))

      val result: Future[Result] = reviewController.onSubmit(id)(fakePostRequest)

      contentType(result) shouldBe Some(MimeTypes.HTML)
    }

    "Return the Http status internal server error when an unexpected error occurs" in new Test {

      MockReviewService
        .approval2iReviewComplete(id, credential, name, ApprovalStatus.WithDesignerForUpdate)
        .returns(Future.successful(Left(InternalServerError)))

      val result: Future[Result] = reviewController.onSubmit(id)(fakePostRequest)

      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
    }

    "Return an Html error page when an unexpected error occurs" in new Test {

      MockReviewService
        .approval2iReviewComplete(id, credential, name, ApprovalStatus.WithDesignerForUpdate)
        .returns(Future.successful(Left(InternalServerError)))

      val result: Future[Result] = reviewController.onSubmit(id)(fakePostRequest)

      contentType(result) shouldBe Some(MimeTypes.HTML)
    }

  }

  "The two eye review controller onPageLoad method" should {

    "display the correct view when the process is in a state to be completed" in new Test {

      MockReviewService
        .approval2iReviewCheck(id)
        .returns(Future.successful(Right(())))

      val result: Future[Result] = reviewController.onPageLoad(id)(fakeGetRequest)

      status(result) shouldBe Status.OK
      contentType(result) shouldBe Some(MimeTypes.HTML)
      contentAsString(result) shouldBe view(id, form)(fakeGetRequest, messages).toString
    }

    "display the error view when the process is not in a state to be completed" in new Test {

      MockReviewService
        .approval2iReviewCheck(id)
        .returns(Future.successful(Left(IncompleteDataError)))

      val result: Future[Result] = reviewController.onPageLoad(id)(fakeGetRequest)

      status(result) shouldBe Status.OK
      contentType(result) shouldBe Some(MimeTypes.HTML)
      contentAsString(result) shouldBe errorView(id)(fakeGetRequest, messages).toString
    }
    "return not found when the process cannot be found" in new Test {

      MockReviewService
        .approval2iReviewCheck(id)
        .returns(Future.successful(Left(NotFoundError)))

      val result: Future[Result] = reviewController.onPageLoad(id)(fakeGetRequest)

      status(result) shouldBe Status.NOT_FOUND
      contentType(result) shouldBe Some(MimeTypes.HTML)
    }
    "return not found when the process data is no longer in a state expected" in new Test {

      MockReviewService
        .approval2iReviewCheck(id)
        .returns(Future.successful(Left(StaleDataError)))

      val result: Future[Result] = reviewController.onPageLoad(id)(fakeGetRequest)

      status(result) shouldBe Status.NOT_FOUND
      contentType(result) shouldBe Some(MimeTypes.HTML)
    }
    "return Internal Server Error when an error is returned" in new Test {

      MockReviewService
        .approval2iReviewCheck(id)
        .returns(Future.successful(Left(InternalServerError)))

      val result: Future[Result] = reviewController.onPageLoad(id)(fakeGetRequest)

      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      contentType(result) shouldBe Some(MimeTypes.HTML)
    }
  }

}
