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

import config.ErrorHandler
import play.api.http.MimeTypes
import play.api.http.Status
import play.api.mvc._
import scala.concurrent.Future

import base.ControllerBaseSpec
import mocks.MockReviewService
import models.ReviewData
import models.errors.{InternalServerError, MalformedResponseError, NotFoundError}
import views.html.twoeye_content_review
import play.api.test.FakeRequest
import play.api.test.Helpers._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import uk.gov.hmrc.http.HeaderCarrier

class TwoEyeReviewControllerSpec extends ControllerBaseSpec with GuiceOneAppPerSuite with MockReviewService {

  private trait Test extends ReviewData {

    implicit val hc = new HeaderCarrier()

    val errorHandler = injector.instanceOf[ErrorHandler]

    val view = injector.instanceOf[twoeye_content_review]

    val reviewController = new TwoEyeReviewController(errorHandler, view, mockReviewService, messagesControllerComponents)

    val fakeRequest = FakeRequest("GET", "/")
  }

  "The two eye review controller" should {

    "Return 200 for a successful retrieval of the review for a process" in new Test {

      MockReviewService.approval2iReview(id).returns(Future.successful(Right(reviewInfo)))

      val result: Future[Result] = reviewController.approval(id)(fakeRequest)

      status(result) shouldBe Status.OK
    }

    "Return an Html document displaying the details of the review" in new Test {

      MockReviewService.approval2iReview(id).returns(Future.successful((Right(reviewInfo))))

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

  }

}
