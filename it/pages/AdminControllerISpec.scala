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
package pages

import models.errors.InvalidProcessError
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSResponse
import stubs.{AuditStub, ExternalGuidanceStub}
import support.IntegrationSpec

class AdminControllerISpec extends IntegrationSpec {

  private val endPoint = "/process/approval"

  "calling the approvalSummaries route" should {

    "return an OK response" in {

      AuditStub.audit()
      val responsePayload: JsValue = Json
        .parse(
          """
            |[
            |  {
            |    "id": "oct09092",
            |    "title": "This is the title",
            |    "lastUpdated": "2017-07-17",
            |    "status": "SubmittedFor2iReview"
            |  }
            |]
          """.stripMargin
        )

      ExternalGuidanceStub.approvalSummary(Status.OK, responsePayload)

      val request = buildRequest(endPoint)
      val response: WSResponse = await(request.get())
      response.status shouldBe Status.OK

    }
    "return a BAD_REQUEST response when the external guidance microservice rejects an invalid process" in {

      AuditStub.audit()

      val responsePayload: JsValue = Json.toJson(InvalidProcessError)
      ExternalGuidanceStub.approvalSummary(Status.OK, responsePayload)

      val request = buildRequest(endPoint)
      val response: WSResponse = await(request.get())
      response.status shouldBe Status.BAD_REQUEST
    }

  }

}