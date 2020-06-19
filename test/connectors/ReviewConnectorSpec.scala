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

package connectors

import java.time.LocalDate

import base.BaseSpec
import mocks.{MockAppConfig, MockHttpClient}
import models._
import models.audit.AuditInfo
import models.errors.{InternalServerError, MalformedResponseError, NotFoundError, StaleDataError}
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReviewConnectorSpec extends BaseSpec {

  private trait Test extends MockHttpClient with FutureAwaits with DefaultAwaitTimeout with ReviewData {

    implicit val hc: HeaderCarrier = HeaderCarrier()

    val connector: ReviewConnector = new ReviewConnector(mockHttpClient, MockAppConfig)

    val reviewStatusChange: ApprovalProcessStatusChange = ApprovalProcessStatusChange("user", "email", ApprovalStatus.ApprovedForPublishing)
    val approvalProcessSummary: ApprovalProcessSummary = ApprovalProcessSummary("id", "title", LocalDate.now, ApprovalStatus.Published)

  }
  private trait TwoEyeReviewTest extends Test {
    val endpoint: String = s"${MockAppConfig.externalGuidanceBaseUrl}/external-guidance/approval/$id/2i-review"
    val pageReviewEndpoint: String = s"${MockAppConfig.externalGuidanceBaseUrl}/external-guidance/approval/$id/2i-page-review"
  }
  private trait FactCheckTest extends Test {
    val endpoint: String = s"${MockAppConfig.externalGuidanceBaseUrl}/external-guidance/approval/$id/fact-check"
    val pageReviewEndpoint: String = s"${MockAppConfig.externalGuidanceBaseUrl}/external-guidance/approval/$id/fact-check-page-review"
  }

  "Calling method approval2iReview with a valid id" should {

    "Return an instance of ApprovalProcessReview for a successful call" in new TwoEyeReviewTest {

      MockedHttpClient
        .get(endpoint)
        .returns(Future.successful(Right(reviewInfo)))

      val response: RequestOutcome[ApprovalProcessReview] =
        await(connector.approval2iReview(id))

      response shouldBe Right(reviewInfo)

    }

    "Return an instance of MalformedResponseError when an error occurs" in new TwoEyeReviewTest {

      MockedHttpClient
        .get(endpoint)
        .returns(Future.successful(Left(MalformedResponseError)))

      val response: RequestOutcome[ApprovalProcessReview] =
        await(connector.approval2iReview(id))

      response shouldBe Left(MalformedResponseError)
    }

    "Return an instance of NotFoundError class when an error occurs" in new TwoEyeReviewTest {

      MockedHttpClient
        .get(endpoint)
        .returns(Future.successful(Left(NotFoundError)))

      val response: RequestOutcome[ApprovalProcessReview] =
        await(connector.approval2iReview(id))

      response shouldBe Left(NotFoundError)
    }

    "Return an instance of StaleDataError class when an error occurs" in new TwoEyeReviewTest {

      MockedHttpClient
        .get(endpoint)
        .returns(Future.successful(Left(StaleDataError)))

      val response: RequestOutcome[ApprovalProcessReview] =
        await(connector.approval2iReview(id))

      response shouldBe Left(StaleDataError)
    }

    "Return an instance of InternalServererror class when an error occurs" in new TwoEyeReviewTest {

      MockedHttpClient
        .get(endpoint)
        .returns(Future.successful(Left(InternalServerError)))

      val response: RequestOutcome[ApprovalProcessReview] =
        await(connector.approval2iReview(id))

      response shouldBe Left(InternalServerError)
    }

  }

  "Calling method approval2iReviewComplete with a valid id and payload" should {

    "Return ApprovalProcessSummary for a successful call" in new TwoEyeReviewTest {

      MockedHttpClient
        .post(endpoint, reviewStatusChange)
        .returns(Future.successful(Right(approvalProcessSummary)))

      val response: RequestOutcome[AuditInfo] =
        await(connector.approval2iReviewComplete(id, reviewStatusChange))

      response shouldBe Right(approvalProcessSummary)

    }

    "Return an instance of MalformedResponseError when an error occurs" in new TwoEyeReviewTest {

      MockedHttpClient
        .post(endpoint, reviewStatusChange)
        .returns(Future.successful(Left(MalformedResponseError)))

      val response: RequestOutcome[AuditInfo] =
        await(connector.approval2iReviewComplete(id, reviewStatusChange))

      response shouldBe Left(MalformedResponseError)
    }

    "Return an instance of NotFoundError class when an error occurs" in new TwoEyeReviewTest {

      MockedHttpClient
        .post(endpoint, reviewStatusChange)
        .returns(Future.successful(Left(NotFoundError)))

      val response: RequestOutcome[AuditInfo] =
        await(connector.approval2iReviewComplete(id, reviewStatusChange))

      response shouldBe Left(NotFoundError)
    }

    "Return an instance of StaleDataError class when an error occurs" in new TwoEyeReviewTest {

      MockedHttpClient
        .post(endpoint, reviewStatusChange)
        .returns(Future.successful(Left(StaleDataError)))

      val response: RequestOutcome[AuditInfo] =
        await(connector.approval2iReviewComplete(id, reviewStatusChange))

      response shouldBe Left(StaleDataError)
    }

    "Return an instance of InternalServererror class when an error occurs" in new TwoEyeReviewTest {

      MockedHttpClient
        .post(endpoint, reviewStatusChange)
        .returns(Future.successful(Left(InternalServerError)))

      val response: RequestOutcome[AuditInfo] =
        await(connector.approval2iReviewComplete(id, reviewStatusChange))

      response shouldBe Left(InternalServerError)
    }

  }

  "Calling method approvalFactCheck with a valid id" should {

    "Return an instance of ApprovalProcessReview for a successful call" in new FactCheckTest {

      MockedHttpClient
        .get(endpoint)
        .returns(Future.successful(Right(reviewInfo)))

      val response: RequestOutcome[ApprovalProcessReview] =
        await(connector.approvalFactCheck(id))

      response shouldBe Right(reviewInfo)

    }

    "Return an instance of MalformedResponseError when an error occurs" in new FactCheckTest {

      MockedHttpClient
        .get(endpoint)
        .returns(Future.successful(Left(MalformedResponseError)))

      val response: RequestOutcome[ApprovalProcessReview] =
        await(connector.approvalFactCheck(id))

      response shouldBe Left(MalformedResponseError)
    }

    "Return an instance of NotFoundError class when an error occurs" in new FactCheckTest {

      MockedHttpClient
        .get(endpoint)
        .returns(Future.successful(Left(NotFoundError)))

      val response: RequestOutcome[ApprovalProcessReview] =
        await(connector.approvalFactCheck(id))

      response shouldBe Left(NotFoundError)
    }

    "Return an instance of StaleDataError class when an error occurs" in new FactCheckTest {

      MockedHttpClient
        .get(endpoint)
        .returns(Future.successful(Left(StaleDataError)))

      val response: RequestOutcome[ApprovalProcessReview] =
        await(connector.approvalFactCheck(id))

      response shouldBe Left(StaleDataError)
    }

    "Return an instance of InternalServererror class when an error occurs" in new FactCheckTest {

      MockedHttpClient
        .get(endpoint)
        .returns(Future.successful(Left(InternalServerError)))

      val response: RequestOutcome[ApprovalProcessReview] =
        await(connector.approvalFactCheck(id))

      response shouldBe Left(InternalServerError)
    }

  }

  "Calling method approvalFactCheckComplete with a valid id and payload" should {

    "Return true for a successful call" in new FactCheckTest {

      MockedHttpClient
        .post(endpoint, reviewStatusChange)
        .returns(Future.successful(Right(approvalProcessSummary)))

      val response: RequestOutcome[AuditInfo] =
        await(connector.approvalFactCheckComplete(id, reviewStatusChange))

      response shouldBe Right(approvalProcessSummary)

    }

    "Return an instance of MalformedResponseError when an error occurs" in new FactCheckTest {

      MockedHttpClient
        .post(endpoint, reviewStatusChange)
        .returns(Future.successful(Left(MalformedResponseError)))

      val response: RequestOutcome[AuditInfo] =
        await(connector.approvalFactCheckComplete(id, reviewStatusChange))

      response shouldBe Left(MalformedResponseError)
    }

    "Return an instance of NotFoundError class when an error occurs" in new FactCheckTest {

      MockedHttpClient
        .post(endpoint, reviewStatusChange)
        .returns(Future.successful(Left(NotFoundError)))

      val response: RequestOutcome[AuditInfo] =
        await(connector.approvalFactCheckComplete(id, reviewStatusChange))

      response shouldBe Left(NotFoundError)
    }

    "Return an instance of StaleDataError class when an error occurs" in new FactCheckTest {

      MockedHttpClient
        .post(endpoint, reviewStatusChange)
        .returns(Future.successful(Left(StaleDataError)))

      val response: RequestOutcome[AuditInfo] =
        await(connector.approvalFactCheckComplete(id, reviewStatusChange))

      response shouldBe Left(StaleDataError)
    }

    "Return an instance of InternalServererror class when an error occurs" in new FactCheckTest {

      MockedHttpClient
        .post(endpoint, reviewStatusChange)
        .returns(Future.successful(Left(InternalServerError)))

      val response: RequestOutcome[AuditInfo] =
        await(connector.approvalFactCheckComplete(id, reviewStatusChange))

      response shouldBe Left(InternalServerError)
    }

  }

  "Calling method approval2iReviewPageInfo with a valid id" should {

    "Return an instance of PageReviewDetail for a successful call" in new TwoEyeReviewTest {

      MockedHttpClient
        .get(s"$pageReviewEndpoint${reviewDetail.pageUrl}")
        .returns(Future.successful(Right(reviewDetail)))

      val response: RequestOutcome[PageReviewDetail] =
        await(connector.approval2iReviewPageInfo(id, reviewDetail.pageUrl))

      response shouldBe Right(reviewDetail)

    }

    "Return an instance of MalformedResponseError when an error occurs" in new TwoEyeReviewTest {

      MockedHttpClient
        .get(s"$pageReviewEndpoint${reviewDetail.pageUrl}")
        .returns(Future.successful(Left(MalformedResponseError)))

      val response: RequestOutcome[PageReviewDetail] =
        await(connector.approval2iReviewPageInfo(id, reviewDetail.pageUrl))

      response shouldBe Left(MalformedResponseError)
    }

    "Return an instance of NotFoundError class when an error occurs" in new TwoEyeReviewTest {

      MockedHttpClient
        .get(s"$pageReviewEndpoint${reviewDetail.pageUrl}")
        .returns(Future.successful(Left(NotFoundError)))

      val response: RequestOutcome[PageReviewDetail] =
        await(connector.approval2iReviewPageInfo(id, reviewDetail.pageUrl))

      response shouldBe Left(NotFoundError)
    }

    "Return an instance of StaleDataError class when an error occurs" in new TwoEyeReviewTest {

      MockedHttpClient
        .get(s"$pageReviewEndpoint${reviewDetail.pageUrl}")
        .returns(Future.successful(Left(StaleDataError)))

      val response: RequestOutcome[PageReviewDetail] =
        await(connector.approval2iReviewPageInfo(id, reviewDetail.pageUrl))

      response shouldBe Left(StaleDataError)
    }

    "Return an instance of InternalServererror class when an error occurs" in new TwoEyeReviewTest {

      MockedHttpClient
        .get(s"$pageReviewEndpoint${reviewDetail.pageUrl}")
        .returns(Future.successful(Left(InternalServerError)))

      val response: RequestOutcome[PageReviewDetail] =
        await(connector.approval2iReviewPageInfo(id, reviewDetail.pageUrl))

      response shouldBe Left(InternalServerError)
    }

  }

  "Calling method approval2iReviewPageComplete with a valid id and payload" should {

    "Return true for a successful call" in new TwoEyeReviewTest {

      MockedHttpClient
        .post(s"$pageReviewEndpoint${updatedReviewDetail.pageUrl}", updatedReviewDetail)
        .returns(Future.successful(Right(true)))

      val response: RequestOutcome[Unit] =
        await(connector.approval2iReviewPageComplete(id, updatedReviewDetail.pageUrl, updatedReviewDetail))

      response shouldBe Right(true)

    }

    "Return an instance of MalformedResponseError when an error occurs" in new TwoEyeReviewTest {

      MockedHttpClient
        .post(s"$pageReviewEndpoint${updatedReviewDetail.pageUrl}", updatedReviewDetail)
        .returns(Future.successful(Left(MalformedResponseError)))

      val response: RequestOutcome[Unit] =
        await(connector.approval2iReviewPageComplete(id, updatedReviewDetail.pageUrl, updatedReviewDetail))

      response shouldBe Left(MalformedResponseError)
    }

    "Return an instance of NotFoundError class when an error occurs" in new TwoEyeReviewTest {

      MockedHttpClient
        .post(s"$pageReviewEndpoint${updatedReviewDetail.pageUrl}", updatedReviewDetail)
        .returns(Future.successful(Left(NotFoundError)))

      val response: RequestOutcome[Unit] =
        await(connector.approval2iReviewPageComplete(id, updatedReviewDetail.pageUrl, updatedReviewDetail))

      response shouldBe Left(NotFoundError)
    }

    "Return an instance of StaleDataError class when an error occurs" in new TwoEyeReviewTest {

      MockedHttpClient
        .post(s"$pageReviewEndpoint${updatedReviewDetail.pageUrl}", updatedReviewDetail)
        .returns(Future.successful(Left(StaleDataError)))

      val response: RequestOutcome[Unit] =
        await(connector.approval2iReviewPageComplete(id, updatedReviewDetail.pageUrl, updatedReviewDetail))

      response shouldBe Left(StaleDataError)
    }

    "Return an instance of InternalServererror class when an error occurs" in new TwoEyeReviewTest {

      MockedHttpClient
        .post(s"$pageReviewEndpoint${updatedReviewDetail.pageUrl}", updatedReviewDetail)
        .returns(Future.successful(Left(InternalServerError)))

      val response: RequestOutcome[Unit] =
        await(connector.approval2iReviewPageComplete(id, updatedReviewDetail.pageUrl, updatedReviewDetail))

      response shouldBe Left(InternalServerError)
    }

  }

  "Calling method factCheckPageInfo with a valid id" should {

    "Return an instance of PageReviewDetail for a successful call" in new FactCheckTest {

      MockedHttpClient
        .get(s"$pageReviewEndpoint${reviewDetail.pageUrl}")
        .returns(Future.successful(Right(reviewDetail)))

      val response: RequestOutcome[PageReviewDetail] =
        await(connector.factCheckPageInfo(id, reviewDetail.pageUrl))

      response shouldBe Right(reviewDetail)

    }

    "Return an instance of MalformedResponseError when an error occurs" in new FactCheckTest {

      MockedHttpClient
        .get(s"$pageReviewEndpoint${reviewDetail.pageUrl}")
        .returns(Future.successful(Left(MalformedResponseError)))

      val response: RequestOutcome[PageReviewDetail] =
        await(connector.factCheckPageInfo(id, reviewDetail.pageUrl))

      response shouldBe Left(MalformedResponseError)
    }

    "Return an instance of NotFoundError class when an error occurs" in new FactCheckTest {

      MockedHttpClient
        .get(s"$pageReviewEndpoint${reviewDetail.pageUrl}")
        .returns(Future.successful(Left(NotFoundError)))

      val response: RequestOutcome[PageReviewDetail] =
        await(connector.factCheckPageInfo(id, reviewDetail.pageUrl))

      response shouldBe Left(NotFoundError)
    }

    "Return an instance of StaleDataError class when an error occurs" in new FactCheckTest {

      MockedHttpClient
        .get(s"$pageReviewEndpoint${reviewDetail.pageUrl}")
        .returns(Future.successful(Left(StaleDataError)))

      val response: RequestOutcome[PageReviewDetail] =
        await(connector.factCheckPageInfo(id, reviewDetail.pageUrl))

      response shouldBe Left(StaleDataError)
    }

    "Return an instance of InternalServererror class when an error occurs" in new FactCheckTest {

      MockedHttpClient
        .get(s"$pageReviewEndpoint${reviewDetail.pageUrl}")
        .returns(Future.successful(Left(InternalServerError)))

      val response: RequestOutcome[PageReviewDetail] =
        await(connector.factCheckPageInfo(id, reviewDetail.pageUrl))

      response shouldBe Left(InternalServerError)
    }

  }

  "Calling method factCheckPageComplete with a valid id and payload" should {

    "Return true for a successful call" in new FactCheckTest {

      MockedHttpClient
        .post(s"$pageReviewEndpoint${updatedReviewDetail.pageUrl}", updatedReviewDetail)
        .returns(Future.successful(Right(true)))

      val response: RequestOutcome[Unit] =
        await(connector.factCheckPageComplete(id, updatedReviewDetail.pageUrl, updatedReviewDetail))

      response shouldBe Right(true)

    }

    "Return an instance of MalformedResponseError when an error occurs" in new FactCheckTest {

      MockedHttpClient
        .post(s"$pageReviewEndpoint${updatedReviewDetail.pageUrl}", updatedReviewDetail)
        .returns(Future.successful(Left(MalformedResponseError)))

      val response: RequestOutcome[Unit] =
        await(connector.factCheckPageComplete(id, updatedReviewDetail.pageUrl, updatedReviewDetail))

      response shouldBe Left(MalformedResponseError)
    }

    "Return an instance of NotFoundError class when an error occurs" in new FactCheckTest {

      MockedHttpClient
        .post(s"$pageReviewEndpoint${updatedReviewDetail.pageUrl}", updatedReviewDetail)
        .returns(Future.successful(Left(NotFoundError)))

      val response: RequestOutcome[Unit] =
        await(connector.factCheckPageComplete(id, updatedReviewDetail.pageUrl, updatedReviewDetail))

      response shouldBe Left(NotFoundError)
    }

    "Return an instance of StaleDataError class when an error occurs" in new FactCheckTest {

      MockedHttpClient
        .post(s"$pageReviewEndpoint${updatedReviewDetail.pageUrl}", updatedReviewDetail)
        .returns(Future.successful(Left(StaleDataError)))

      val response: RequestOutcome[Unit] =
        await(connector.factCheckPageComplete(id, updatedReviewDetail.pageUrl, updatedReviewDetail))

      response shouldBe Left(StaleDataError)
    }

    "Return an instance of InternalServererror class when an error occurs" in new FactCheckTest {

      MockedHttpClient
        .post(s"$pageReviewEndpoint${updatedReviewDetail.pageUrl}", updatedReviewDetail)
        .returns(Future.successful(Left(InternalServerError)))

      val response: RequestOutcome[Unit] =
        await(connector.factCheckPageComplete(id, updatedReviewDetail.pageUrl, updatedReviewDetail))

      response shouldBe Left(InternalServerError)
    }

  }

}
