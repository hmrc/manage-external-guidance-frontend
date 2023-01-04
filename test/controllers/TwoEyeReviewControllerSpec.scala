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

package controllers

import base.ControllerBaseSpec
import config.ErrorHandler
import controllers.actions.FakeTwoEyeReviewerAction
import mocks.MockReviewService
import models.ReviewData
import models.errors._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.{MimeTypes, Status}
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import views.html.{duplicate_process_code_error, twoeye_content_review}

import scala.concurrent.Future

class TwoEyeReviewControllerSpec extends ControllerBaseSpec with GuiceOneAppPerSuite with MockReviewService {

  private trait Test extends ReviewData {

    implicit val hc = HeaderCarrier()

    val errorHandler = injector.instanceOf[ErrorHandler]

    val view = injector.instanceOf[twoeye_content_review]
    val duplicateView = injector.instanceOf[duplicate_process_code_error]

    val reviewController =
      new TwoEyeReviewController(errorHandler, FakeTwoEyeReviewerAction, view, duplicateView, mockReviewService, messagesControllerComponents)

    val fakeRequest = FakeRequest("GET", "/")
  }

  "The two eye review controller" should {

    "Return 200 for a successful retrieval of the review for a process" in new Test {

      MockReviewService.approval2iReview(id).returns(Future.successful(Right(reviewInfo)))

      val result: Future[Result] = reviewController.approval(id)(fakeRequest)

      status(result) shouldBe Status.OK
    }

    "Return an Html document displaying the details of the review" in new Test {

      MockReviewService.approval2iReview(id).returns(Future.successful(Right(reviewInfo)))

      val result: Future[Result] = reviewController.approval(id)(fakeRequest)

      contentType(result) shouldBe Some(MimeTypes.HTML)
    }

    "Return the Http status Not found when the process review does not exist" in new Test {

      MockReviewService.approval2iReview(id).returns(Future.successful(Left(NotFoundError)))

      val result: Future[Result] = reviewController.approval(id)(fakeRequest)

      status(result) shouldBe Status.NOT_FOUND
    }

    "Return an Html error page when the process review does not exist" in new Test {

      MockReviewService.approval2iReview(id).returns(Future.successful(Left(NotFoundError)))

      val result: Future[Result] = reviewController.approval(id)(fakeRequest)

      contentType(result) shouldBe Some(MimeTypes.HTML)
    }

    "Return the Http status internal server error when the process review data returned to the application is malformed" in new Test {

      MockReviewService.approval2iReview(id).returns(Future.successful(Left(MalformedResponseError)))

      val result: Future[Result] = reviewController.approval(id)(fakeRequest)

      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
    }

    "Return an Html error page when the process review data returned to the application is malformed" in new Test {

      MockReviewService.approval2iReview(id).returns(Future.successful(Left(MalformedResponseError)))

      val result: Future[Result] = reviewController.approval(id)(fakeRequest)

      contentType(result) shouldBe Some(MimeTypes.HTML)
    }

    "Return the Http status not found when the process review data requested is out of date in some way" in new Test {

      MockReviewService.approval2iReview(id).returns(Future.successful(Left(StaleDataError)))

      val result: Future[Result] = reviewController.approval(id)(fakeRequest)

      status(result) shouldBe Status.NOT_FOUND
    }

    "Return an Html error page when the process review data requested is out of date in some way" in new Test {

      MockReviewService.approval2iReview(id).returns(Future.successful(Left(StaleDataError)))

      val result: Future[Result] = reviewController.approval(id)(fakeRequest)

      contentType(result) shouldBe Some(MimeTypes.HTML)
    }

    "Return the Http status internal server error when an unexpected error occurs" in new Test {

      MockReviewService.approval2iReview(id).returns(Future.successful(Left(InternalServerError)))

      val result: Future[Result] = reviewController.approval(id)(fakeRequest)

      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
    }

    "Return an Html error page when an unexpected error occurs" in new Test {

      MockReviewService.approval2iReview(id).returns(Future.successful(Left(InternalServerError)))

      val result: Future[Result] = reviewController.approval(id)(fakeRequest)

      contentType(result) shouldBe Some(MimeTypes.HTML)
    }

    "Return the Http status BadRequest and display a view when the system identifies a duplicate" in new Test {

      MockReviewService.approval2iReview(id).returns(Future.successful(Left(DuplicateKeyError)))

      val result: Future[Result] = reviewController.approval(id)(fakeRequest)

      status(result) shouldBe Status.BAD_REQUEST
      contentType(result) shouldBe Some(MimeTypes.HTML)
    }


  }

}
