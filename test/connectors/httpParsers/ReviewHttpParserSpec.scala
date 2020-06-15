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

package connectors.httpParsers

import models.errors.{InternalServerError, MalformedResponseError, NotFoundError, StaleDataError}
import models.{ApprovalProcessReview, PageReviewDetail, PageReviewStatus, RequestOutcome, ReviewData}
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpResponse

class ReviewHttpParserSpec extends WordSpec with Matchers with ReviewData {

  import connectors.httpParsers.ReviewHttpParser._

  class GetReviewDetailsSetup(status: Int, optJson: Option[JsValue] = None) {
    private val httpMethod = "GET"
    private val url = "/"
    val httpResponse = HttpResponse(status, optJson, Map())

    def readResponse: RequestOutcome[ApprovalProcessReview] =
      getReviewDetailsHttpReads.read(httpMethod, url, httpResponse)
  }

  "The ReviewHttpParser.getReviewDetailsHttpReads" when {
    "the response is OK" when {
      "the json successfully deserializes to the model" should {
        "Return ApprovalProcessReview" in new GetReviewDetailsSetup(OK, Some(Json.toJson(reviewInfo))) {
          readResponse shouldBe Right(reviewInfo)
        }
      }
      "the json is malformed" should {
        "return MalformedResponseError" in new GetReviewDetailsSetup(OK, Some(Json.obj())) {
          readResponse shouldBe Left(MalformedResponseError)
        }
      }
    }
    "the response is NOT_FOUND with a code of NOT_FOUND_ERROR" should {
      "return NotFoundError" in new GetReviewDetailsSetup(NOT_FOUND, Some(Json.obj("code" -> "NOT_FOUND_ERROR", "message" -> ""))) {
        readResponse shouldBe Left(NotFoundError)
      }
    }
    "the response is NOT_FOUND with a code of STALE_DATA_ERROR" should {
      "return StaleDataError" in new GetReviewDetailsSetup(NOT_FOUND, Some(Json.obj("code" -> "STALE_DATA_ERROR", "message" -> ""))) {
        readResponse shouldBe Left(StaleDataError)
      }
    }
    "the response is NOT_FOUND and has malformed error json" should {
      "return MalformedResponseError" in new GetReviewDetailsSetup(NOT_FOUND, Some(Json.obj("code" -> "NOT_FOUND_ERROR"))) {
        readResponse shouldBe Left(MalformedResponseError)
      }
    }
    "the response is anything else" should {
      "return InternalServerError" in new GetReviewDetailsSetup(INTERNAL_SERVER_ERROR) {
        readResponse shouldBe Left(InternalServerError)
      }
    }
  }

  class PostReviewCompleteSetup(status: Int, optJson: Option[JsValue] = None) {
    private val httpMethod = "GET"
    private val url = "/"
    val httpResponse = HttpResponse(status, optJson, Map())

    def readResponse: RequestOutcome[Unit] =
      postReviewCompleteHttpReads.read(httpMethod, url, httpResponse)
  }

  "The ReviewHttpParser.postReviewCompleteHttpReads" when {
    "the response is NO_CONTENT" should {
      "Return Unit" in new PostReviewCompleteSetup(NO_CONTENT) {
        readResponse shouldBe Right(())
      }
    }
    "the response is NOT_FOUND with a code of NOT_FOUND_ERROR" should {
      "return NotFoundError" in new PostReviewCompleteSetup(NOT_FOUND, Some(Json.obj("code" -> "NOT_FOUND_ERROR", "message" -> ""))) {
        readResponse shouldBe Left(NotFoundError)
      }
    }
    "the response is NOT_FOUND with a code of STALE_DATA_ERROR" should {
      "return StaleDataError" in new PostReviewCompleteSetup(NOT_FOUND, Some(Json.obj("code" -> "STALE_DATA_ERROR", "message" -> ""))) {
        readResponse shouldBe Left(StaleDataError)
      }
    }
    "the response is NOT_FOUND and has malformed error json" should {
      "return MalformedResponseError" in new PostReviewCompleteSetup(NOT_FOUND, Some(Json.obj("code" -> "NOT_FOUND_ERROR"))) {
        readResponse shouldBe Left(MalformedResponseError)
      }
    }
    "the response is anything else" should {
      "return InternalServerError" in new PostReviewCompleteSetup(INTERNAL_SERVER_ERROR, Some(Json.obj("code" -> "INTERNAL_SERVER_ERROR", "message" -> ""))) {
        readResponse shouldBe Left(InternalServerError)
      }
    }
  }

  "The ReviewHttpParser.getReviewPageDetailsHttpReads" when {
    val pageUrl = "pageUrl"
    val pageReviewDetail = PageReviewDetail(id, pageUrl, None, PageReviewStatus.NotStarted)

    class GetReviewPageDetailsSetup(status: Int, optJson: Option[JsValue] = None) {
      private val httpMethod = "GET"
      private val url = "/"
      val httpResponse = HttpResponse(status, optJson, Map())

      def readResponse: RequestOutcome[PageReviewDetail] =
        getReviewPageDetailsHttpReads.read(httpMethod, url, httpResponse)
    }

    "the response is OK" when {
      "the json successfully deserializes to the model" should {
        "Return ApprovalProcessReview" in new GetReviewPageDetailsSetup(OK, Some(Json.toJson(pageReviewDetail))) {
          readResponse shouldBe Right(pageReviewDetail)
        }
      }
      "the json is malformed" should {
        "return MalformedResponseError" in new GetReviewPageDetailsSetup(OK, Some(Json.obj())) {
          readResponse shouldBe Left(MalformedResponseError)
        }
      }
    }
    "the response is NOT_FOUND with a code of NOT_FOUND_ERROR" should {
      "return NotFoundError" in new GetReviewPageDetailsSetup(NOT_FOUND, Some(Json.obj("code" -> "NOT_FOUND_ERROR", "message" -> ""))) {
        readResponse shouldBe Left(NotFoundError)
      }
    }
    "the response is NOT_FOUND with a code of STALE_DATA_ERROR" should {
      "return StaleDataError" in new GetReviewPageDetailsSetup(NOT_FOUND, Some(Json.obj("code" -> "STALE_DATA_ERROR", "message" -> ""))) {
        readResponse shouldBe Left(StaleDataError)
      }
    }
    "the response is NOT_FOUND and has malformed error json" should {
      "return MalformedResponseError" in new GetReviewPageDetailsSetup(NOT_FOUND, Some(Json.obj("code" -> "NOT_FOUND_ERROR"))) {
        readResponse shouldBe Left(MalformedResponseError)
      }
    }
    "the response is anything else" should {
      "return InternalServerError" in new GetReviewPageDetailsSetup(INTERNAL_SERVER_ERROR) {
        readResponse shouldBe Left(InternalServerError)
      }
    }
  }
}
