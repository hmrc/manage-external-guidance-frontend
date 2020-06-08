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

import base.BaseSpec
import mocks.MockReviewConnector
import models._
import models.errors.InternalServerError
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class ReviewServiceSpec extends BaseSpec {

  private trait Test extends MockReviewConnector with ReviewData {

    implicit val hc = HeaderCarrier()

    lazy val reviewService = new ReviewService(mockReviewConnector)
  }

  "The review service" when {
    "calling the approval2iReview method" should {
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

    "calling the approval2iReviewComplete method" should {
      "Return no content after a successful call to the review connector" in new Test {

        val info = ApprovalProcessStatusChange("userPid", "userName", ApprovalStatus.ApprovedForPublishing)
        MockReviewConnector
          .approval2iReviewComplete(id, info)
          .returns(Future.successful(Right(())))

        val result: Future[RequestOutcome[Unit]] = reviewService.approval2iReviewComplete(id, "userPid", "userName", ApprovalStatus.ApprovedForPublishing)

        result.onComplete {
          case Success(response) =>
            response match {
              case Right(()) => succeed
              case Left(error) => fail(s"Unexpected error returned by mock review connector : ${error.toString}")
            }
          case Failure(exception) => fail(s"Future onComplete returned unexpected error : ${exception.getMessage}")
        }
      }

      "Return an error following an unsuccessful call to the connector" in new Test {

        val info = ApprovalProcessStatusChange("userPid", "userName", ApprovalStatus.ApprovedForPublishing)
        MockReviewConnector
          .approval2iReviewComplete(id, info)
          .returns(Future.successful(Left(InternalServerError)))

        val result: Future[RequestOutcome[Unit]] = reviewService.approval2iReviewComplete(id, "userPid", "userName", info.status)

        result.onComplete {
          case Success(response) =>
            response match {
              case Right(_) => fail("Approval process review returned when error expected")
              case Left(error) => error shouldBe InternalServerError
            }
          case Failure(exception) => fail(s"Future onComplete returned unexpected error : ${exception.getMessage}")
        }
      }
    }

    "calling the approval2iPageReview method" should {
      "Return an instance of the class ApprovalProcessReview after a successful call to the review connector" in new Test {

        val pageUrl = "pageUrl"
        val pageReviewDetail = PageReviewDetail(id, pageUrl, None, PageReviewStatus.NotStarted)
        MockReviewConnector
          .approval2iReviewPageInfo(id, "pageUrl")
          .returns(Future.successful(Right(pageReviewDetail)))

        val result: Future[RequestOutcome[PageReviewDetail]] = reviewService.approval2iPageReview(id, pageUrl)

        result.onComplete {
          case Success(response) =>
            response match {
              case Right(pageReview) => pageReviewDetail shouldBe pageReview
              case Left(error) => fail(s"Unexpected error returned by mock review connector : ${error.toString}")
            }
          case Failure(exception) => fail(s"Future onComplete returned unexpected error : ${exception.getMessage}")
        }
      }
    }

    "calling the approval2iPageReviewComplete method" should {
      "Return no content after a successful call to the review connector" in new Test {

        val pageUrl = "pageUrl"
        val info = PageReviewDetail(id, pageUrl, None, PageReviewStatus.NotStarted)
        MockReviewConnector
          .approval2iReviewPageComplete(id, pageUrl, info)
          .returns(Future.successful(Right(())))

        val result: Future[RequestOutcome[Unit]] = reviewService.approval2iPageReviewComplete(id, pageUrl, info)

        result.onComplete {
          case Success(response) =>
            response match {
              case Right(()) => succeed
              case Left(error) => fail(s"Unexpected error returned by mock review connector : ${error.toString}")
            }
          case Failure(exception) => fail(s"Future onComplete returned unexpected error : ${exception.getMessage}")
        }
      }
    }

    "calling the approvalFactCheck method" should {
      "Return an instance of the class ApprovalProcessReview after a successful call to the review connector" in new Test {

        MockReviewConnector
          .approvalFactCheck(id)
          .returns(Future.successful(Right(reviewInfo)))

        val result: Future[RequestOutcome[ApprovalProcessReview]] = reviewService.approvalFactCheck(id)

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
          .approvalFactCheck(id)
          .returns(Future.successful(Left(InternalServerError)))

        val result: Future[RequestOutcome[ApprovalProcessReview]] = reviewService.approvalFactCheck(id)

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

    "calling the approvalFactCheckComplete method" should {
      "Return no content after a successful call to the review connector" in new Test {

        val info = ApprovalProcessStatusChange("userPid", "userName", ApprovalStatus.WithDesignerForUpdate)
        MockReviewConnector
          .approvalFactCheckComplete(id, info)
          .returns(Future.successful(Right(())))

        val result: Future[RequestOutcome[Unit]] = reviewService.approvalFactCheckComplete(id, "userPid", "userName", ApprovalStatus.WithDesignerForUpdate)

        result.onComplete {
          case Success(response) =>
            response match {
              case Right(()) => succeed
              case Left(error) => fail(s"Unexpected error returned by mock review connector : ${error.toString}")
            }
          case Failure(exception) => fail(s"Future onComplete returned unexpected error : ${exception.getMessage}")
        }
      }

      "Return an error following an unsuccessful call to the connector" in new Test {

        val info = ApprovalProcessStatusChange("userPid", "userName", ApprovalStatus.ApprovedForPublishing)
        MockReviewConnector
          .approvalFactCheckComplete(id, info)
          .returns(Future.successful(Left(InternalServerError)))

        val result: Future[RequestOutcome[Unit]] = reviewService.approvalFactCheckComplete(id, "userPid", "userName", info.status)

        result.onComplete {
          case Success(response) =>
            response match {
              case Right(_) => fail("Approval process review returned when error expected")
              case Left(error) => error shouldBe InternalServerError
            }
          case Failure(exception) => fail(s"Future onComplete returned unexpected error : ${exception.getMessage}")
        }
      }
    }

  }

}
