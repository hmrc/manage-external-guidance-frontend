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

package services

import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future
import scala.util.{Failure, Success}

import models.{ApprovalProcessReview, RequestOutcome, ReviewData}
import models.errors.InternalServerError

import uk.gov.hmrc.http.HeaderCarrier

import base.BaseSpec
import mocks.MockReviewConnector

class ReviewServiceSpec extends BaseSpec {

  private trait Test extends MockReviewConnector with ReviewData {

    implicit val hc = new HeaderCarrier()

    lazy val reviewService = new ReviewService(mockReviewConnector)
  }

  "The review service" should {

    "Return an instance of the class ApprovalProcessReview after a successful call to the review connector" in new Test {

      MockReviewConnector
        .approval2iReview(id)
        .returns(Future.successful(Right(reviewInfo)))

      val result: Future[RequestOutcome[ApprovalProcessReview]] = reviewService.approval2iReview(id)

      result.onComplete {
        case Success(response) =>
          response match {
            case Right(approvalProcessReview) => approvalProcessReview shouldBe reviewInfo
            case Left(error) => fail(s"Unexpected error returned by mock review connector : ${error.toString}")
          }
        case Failure(exception) => fail(s"Future onComplete returned unexpected error : ${exception.getMessage}")
      }
    }

    "Return an error following an unsuccessful call to the connector" in new Test {

      MockReviewConnector
        .approval2iReview(id)
        .returns(Future.successful(Left(InternalServerError)))

      val result: Future[RequestOutcome[ApprovalProcessReview]] = reviewService.approval2iReview(id)

      result.onComplete {
        case Success(response) =>
          response match {
            case Right(approvalProcessReview) => fail("Approval process review returned when error expected")
            case Left(error) => error shouldBe InternalServerError
          }
        case Failure(exception) => fail(s"Future onComplete returned unexpected error : ${exception.getMessage}")
      }
    }

  }
}
