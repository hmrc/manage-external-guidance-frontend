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

import java.util.UUID.randomUUID

import base.BaseSpec
import connectors.httpParsers.SaveSubmittedProcessHttpParser.saveSubmittedProcessHttpReads
import models.errors.{InternalServerError, InvalidProcessError}
import models.{RequestOutcome, SaveSubmittedProcessResponse}
import play.api.http.{HttpVerbs, Status}
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HttpResponse

class SaveSubmittedProcessHttpParserSpec extends BaseSpec with HttpVerbs with Status {

  private trait Test {

    val url: String = "/test"

    val id: String = randomUUID().toString

    val validResponse: JsValue = Json.obj("id" -> id)

    val invalidResponse: JsValue = Json.obj() // no "id" property
  }

  "Parsing a successful response" should {

    "return a valid submitted process submission response" in new Test {

      private val httpResponse = HttpResponse(CREATED, Some(validResponse))
      private val result = saveSubmittedProcessHttpReads.read(POST, url, httpResponse)
      result shouldBe Right(SaveSubmittedProcessResponse(id))
    }
  }

  "Parsing an error response" should {

    "return an invalid process error for a bad request" in new Test {

      val httpResponse: HttpResponse = HttpResponse(BAD_REQUEST)

      val result: RequestOutcome[SaveSubmittedProcessResponse] =
        saveSubmittedProcessHttpReads.read(POST, url, httpResponse)

      result shouldBe Left(InvalidProcessError)
    }

    "return an internal server error for an invalid response" in new Test {

      val httpResponse: HttpResponse = HttpResponse(CREATED, Some(invalidResponse))

      val result: RequestOutcome[SaveSubmittedProcessResponse] =
        saveSubmittedProcessHttpReads.read(POST, url, httpResponse)

      result shouldBe Left(InternalServerError)
    }

    "return an internal server error when an error distinct from invalid process occurs" in new Test {

      val httpResponse: HttpResponse = HttpResponse(SERVICE_UNAVAILABLE)

      val result: RequestOutcome[SaveSubmittedProcessResponse] =
        saveSubmittedProcessHttpReads.read(POST, url, httpResponse)

      result shouldBe Left(InternalServerError)
    }
  }
}