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

import base.BaseSpec
import mocks.{MockAppConfig, MockHttpClient}
import models.errors.{InternalServerError, MalformedResponseError, NotFoundError, StaleDataError}
import models.{ApprovalProcessReview, ApprovalProcessStatusChange, RequestOutcome, ReviewData}
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReviewConnectorSpec extends BaseSpec {

  private trait ReviewInfoTest extends MockHttpClient with FutureAwaits with DefaultAwaitTimeout with ReviewData {

    implicit val hc: HeaderCarrier = HeaderCarrier()

    val connector: ReviewConnector = new ReviewConnector(mockHttpClient, MockAppConfig)
    val endpoint: String = s"${MockAppConfig.externalGuidanceBaseUrl}/external-guidance/approval/$id/2i-review"

    val reviewStatusChange = ApprovalProcessStatusChange("user", "email", "newStatus")
  }

  "Calling method approval2iReview with a valid id" should {

    "Return an instance of ApprovalProcessReview for a successful call" in new ReviewInfoTest {

      MockedHttpClient
        .get(endpoint)
        .returns(Future.successful(Right(reviewInfo)))

      val response: RequestOutcome[ApprovalProcessReview] =
        await(connector.approval2iReview(id))

      response shouldBe Right(reviewInfo)

    }

    "Return an instance of MalformedResponseError when an error occurs" in new ReviewInfoTest {

      MockedHttpClient
        .get(endpoint)
        .returns(Future.successful(Left(MalformedResponseError)))

      val response: RequestOutcome[ApprovalProcessReview] =
        await(connector.approval2iReview(id))

      response shouldBe Left(MalformedResponseError)
    }

    "Return an instance of NotFoundError class when an error occurs" in new ReviewInfoTest {

      MockedHttpClient
        .get(endpoint)
        .returns(Future.successful(Left(NotFoundError)))

      val response: RequestOutcome[ApprovalProcessReview] =
        await(connector.approval2iReview(id))

      response shouldBe Left(NotFoundError)
    }

    "Return an instance of StaleDataError class when an error occurs" in new ReviewInfoTest {

      MockedHttpClient
        .get(endpoint)
        .returns(Future.successful(Left(StaleDataError)))

      val response: RequestOutcome[ApprovalProcessReview] =
        await(connector.approval2iReview(id))

      response shouldBe Left(StaleDataError)
    }

    "Return an instance of InternalServererror class when an error occurs" in new ReviewInfoTest {

      MockedHttpClient
        .get(endpoint)
        .returns(Future.successful(Left(InternalServerError)))

      val response: RequestOutcome[ApprovalProcessReview] =
        await(connector.approval2iReview(id))

      response shouldBe Left(InternalServerError)
    }

  }

  "Calling method approval2iReviewComplete with a valid id and payload" should {

    "Return true for a successful call" in new ReviewInfoTest {

      MockedHttpClient
        .post(endpoint, reviewStatusChange)
        .returns(Future.successful(Right(true)))

      val response: RequestOutcome[Boolean] =
        await(connector.approval2iReviewComplete(id, reviewStatusChange))

      response shouldBe Right(true)

    }

    "Return an instance of MalformedResponseError when an error occurs" in new ReviewInfoTest {

      MockedHttpClient
        .post(endpoint, reviewStatusChange)
        .returns(Future.successful(Left(MalformedResponseError)))

      val response: RequestOutcome[Boolean] =
        await(connector.approval2iReviewComplete(id, reviewStatusChange))

      response shouldBe Left(MalformedResponseError)
    }

    "Return an instance of NotFoundError class when an error occurs" in new ReviewInfoTest {

      MockedHttpClient
        .post(endpoint, reviewStatusChange)
        .returns(Future.successful(Left(NotFoundError)))

      val response: RequestOutcome[Boolean] =
        await(connector.approval2iReviewComplete(id, reviewStatusChange))

      response shouldBe Left(NotFoundError)
    }

    "Return an instance of StaleDataError class when an error occurs" in new ReviewInfoTest {

      MockedHttpClient
        .post(endpoint, reviewStatusChange)
        .returns(Future.successful(Left(StaleDataError)))

      val response: RequestOutcome[Boolean] =
        await(connector.approval2iReviewComplete(id, reviewStatusChange))

      response shouldBe Left(StaleDataError)
    }

    "Return an instance of InternalServererror class when an error occurs" in new ReviewInfoTest {

      MockedHttpClient
        .post(endpoint, reviewStatusChange)
        .returns(Future.successful(Left(InternalServerError)))

      val response: RequestOutcome[Boolean] =
        await(connector.approval2iReviewComplete(id, reviewStatusChange))

      response shouldBe Left(InternalServerError)
    }

  }
}
