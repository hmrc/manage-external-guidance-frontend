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
import connectors.httpParsers.ReviewHttpParser._
import mocks.{MockAppConfig, MockHttpClient}
import models.ReviewData
import org.scalatest.concurrent.ScalaFutures
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReviewConnectorSpec extends BaseSpec {

  private trait ReviewInfoTest extends MockHttpClient with FutureAwaits with DefaultAwaitTimeout with ReviewData {

    implicit val hc: HeaderCarrier = HeaderCarrier()

    val id: String = "Oct90005"

    val connector: ReviewConnector = new ReviewConnector(mockHttpClient, MockAppConfig)
    val endpoint: String = s"${MockAppConfig.externalGuidanceBaseUrl}/external-guidance/approval/$id/2i-review"

  }

  "Calling method approval2iReview with a valid id" should {

    "Return an instance of ReviewDetailsSuccess for a successful call" in new ReviewInfoTest {

      MockedHttpClient
        .get(endpoint)
        .returns(Future.successful(Right(reviewInfo)))

      val response: GetReviewDetailsResponse =
        await(connector.approval2iReview(id))

      response shouldBe Right(reviewInfo)

    }

    "Return an instance of ReviewDetailsMalformed when an error occurs" in new ReviewInfoTest {

      MockedHttpClient
        .get(endpoint)
        .returns(Future.successful(Left(ReviewDetailsMalformed)))

      val response: GetReviewDetailsResponse =
        await(connector.approval2iReview(id))

      response shouldBe Left(ReviewDetailsMalformed)
    }

    "Return an instance of ReviewDetailsNotFound class when an error occurs" in new ReviewInfoTest {

      MockedHttpClient
        .get(endpoint)
        .returns(Future.successful(Left(ReviewDetailsNotFound)))

      val response: GetReviewDetailsResponse =
        await(connector.approval2iReview(id))

      response shouldBe Left(ReviewDetailsNotFound)
    }
//
//    "Return an instance of ReviewDetailsStale class when an error occurs" in new ReviewInfoTest {
//
//      MockedHttpClient
//        .get(endpoint)(ReviewDetailsStale)
//
//      val response: GetReviewDetailsResponse =
//        await(connector.approval2iReview(id))
//
//      response shouldBe Left(ReviewDetailsStale)
//    }
//
//    "Return an instance of ReviewDetailsFailure class when an error occurs" in new ReviewInfoTest {
//
//      MockedHttpClient
//        .get(endpoint)(ReviewDetailsFailure(503))
//
//      val response: GetReviewDetailsResponse =
//        await(connector.approval2iReview(id))
//
//      response shouldBe Left(ReviewDetailsFailure(503))
//    }

  }

}
