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

import models.ReviewData
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpResponse

class ReviewHttpParserSpec extends WordSpec with Matchers with ReviewData {

  import connectors.httpParsers.ReviewHttpParser._

  class Setup(status: Int, optJson: Option[JsValue] = None) {
    private val httpMethod = "GET"
    private val url = "/"
    val httpResponse = HttpResponse(status, optJson, Map())

    def readResponse: ReviewHttpParser.GetReviewDetailsResponse =
      getReviewDetailsHttpReads.read(httpMethod, url, httpResponse)
  }

  "The ReviewHttpParser" when {
    "the response is OK" when {
      "the json successfully deserializes to the model" should {
        "Return ReviewDetailsSuccess" in new Setup(OK, Some(Json.toJson(reviewInfo))) {
          readResponse shouldBe Right(ReviewDetailsSuccess(reviewInfo))
        }
      }
      "the json is malformed" should {
        "return ReviewDetailsMalformed" in new Setup(OK, Some(Json.obj())) {
          readResponse shouldBe Left(ReviewDetailsMalformed)
        }
      }
    }
    "the response is NOT_FOUND with a code of NOT_FOUND_ERROR" should {
      "return ReviewDetailsNotFound" in new Setup(NOT_FOUND, Some(Json.obj("code" -> "NOT_FOUND_ERROR", "message" -> ""))) {
        readResponse shouldBe Left(ReviewDetailsNotFound)
      }
    }
    "the response is NOT_FOUND with a code of STALE_DATA_ERROR" should {
      "return ReviewDetailsStale" in new Setup(NOT_FOUND, Some(Json.obj("code" -> "STALE_DATA_ERROR", "message" -> ""))) {
        readResponse shouldBe Left(ReviewDetailsStale)
      }
    }
    "the response is NOT_FOUND and has malformed error json" should {
      "return ReviewDetailsMalformed" in new Setup(NOT_FOUND, Some(Json.obj("code" -> "NOT_FOUND_ERROR"))) {
        readResponse shouldBe Left(ReviewDetailsMalformed)
      }
    }
    "the response is anything else" should {
      "return ReviewDetailsFailure" in new Setup(INTERNAL_SERVER_ERROR) {
        readResponse shouldBe Left(ReviewDetailsFailure(INTERNAL_SERVER_ERROR))
      }
    }
  }

}
