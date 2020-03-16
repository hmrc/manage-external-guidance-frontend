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

import connectors.httpParsers.SaveScratchProcessHttpParser.saveScratchProcessHttpReads

import java.util.UUID.randomUUID

import play.api.http.{HttpVerbs, Status}
import play.api.libs.json.{Json, JsValue}

import uk.gov.hmrc.http.HttpResponse

import models.{RequestOutcome, ScratchProcessSubmissionResponse}
import models.errors.{ExternalGuidanceServiceError, InvalidProcessError, InternalServerError}

import base.BaseSpec

class SaveScratchProcessHttpParserSpec extends BaseSpec with HttpVerbs with Status {

  private trait Test {

    val url: String = "/test"

    val id: String = randomUUID().toString

    val validResponseAsString: String = "{ \"id\":\"" + id + "\" }"

    val validResponse: JsValue = Json.parse(validResponseAsString)

    val invalidJsonAsString: String = "{ \"Error\":\"500\", \"Message\":\"Internal server error\" }"

    val invalidResponse: JsValue = Json.parse(invalidJsonAsString)
  }

  "Parsing a 201 successful scratch process submission" should {

    "Return a valid scratch process submission response" in new Test {

      val httpResponse: HttpResponse = HttpResponse(CREATED, Some(validResponse))

      val result: RequestOutcome[ScratchProcessSubmissionResponse] =
        saveScratchProcessHttpReads.read(POST, url, httpResponse)

      result shouldBe Right(ScratchProcessSubmissionResponse(id))
    }
  }

  "Parsing an erroneous return" should {

    "Return an invalid process error for a bad request" in new Test {

      val httpResponse: HttpResponse = HttpResponse(BAD_REQUEST)

      val result: RequestOutcome[ScratchProcessSubmissionResponse] =
        saveScratchProcessHttpReads.read(POST, url, httpResponse)

      result shouldBe Left(InvalidProcessError)
    }

    "Return an external guidance service error for an invalid response" in new Test {

      val httpResponse: HttpResponse = HttpResponse(CREATED, Some(invalidResponse))

      val result: RequestOutcome[ScratchProcessSubmissionResponse] =
        saveScratchProcessHttpReads.read(POST, url, httpResponse)

      result shouldBe Left(ExternalGuidanceServiceError)
    }

    "Return an external guidance service error for an empty response" in new Test {

      val httpResponse: HttpResponse = HttpResponse(CREATED)

      val result: RequestOutcome[ScratchProcessSubmissionResponse] =
        saveScratchProcessHttpReads.read(POST, url, httpResponse)

      result shouldBe Left(ExternalGuidanceServiceError)
    }

    "Return an internal server error when an error distinct from invalid process occurs" in new Test {

      val httpResponse: HttpResponse = HttpResponse(SERVICE_UNAVAILABLE)

      val result: RequestOutcome[ScratchProcessSubmissionResponse] =
        saveScratchProcessHttpReads.read(POST, url, httpResponse)

      result shouldBe Left(InternalServerError)
    }
  }
}
