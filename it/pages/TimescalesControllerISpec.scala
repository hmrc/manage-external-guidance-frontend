/*
 * Copyright 2022 HM Revenue & Customs
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

import models.{UpdateDetails, TimescalesResponse}
import models.errors.{Error, BadRequestError, InternalServerError, NotFoundError}
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import stubs.{AuditStub, AuthStub, ExternalGuidanceStub}
import support.IntegrationSpec
import java.time.{ZoneOffset, ZonedDateTime}

class TimescalesControllerISpec extends IntegrationSpec {
  val when: ZonedDateTime = ZonedDateTime.of(2022, 3, 11, 10, 30, 0, 0, ZoneOffset.UTC)
  val updateDetails: UpdateDetails = UpdateDetails(when, "34567822", "UserName", "user@email.co.uk", Nil)
  val timescaleResponse: TimescalesResponse = TimescalesResponse(650, Some(updateDetails))
  val timescaleResponseJson: JsValue = Json.toJson(timescaleResponse)

  "Calling the approval 2i review endpoint with a valid process identifier" should {

    "return an Ok response" in {

      AuditStub.audit()
      AuthStub.authorise()

      ExternalGuidanceStub.timescales(Status.OK, timescaleResponseJson)

      val request: WSRequest = buildRequest("/timescales")

      val response: WSResponse = await(request.get())

      response.status shouldBe Status.OK

      response.body.contains("Last update received from UserName on 11 Mar 2022 at 10:30 AM UTC") shouldBe true
    }

    "return a InternalServerError response when the external guidance service returns not found" in {

      AuditStub.audit()
      AuthStub.authorise()

      val responsePayload: JsValue = Json.toJson[Error](NotFoundError)

      ExternalGuidanceStub.timescales(Status.NOT_FOUND, responsePayload)

      val request = buildRequest("/timescales")

      val response: WSResponse = await(request.get())

      response.status shouldBe Status.INTERNAL_SERVER_ERROR
    }

    "return an internal server error response when an unexpected error is returned by the external guidance service" in {

      AuditStub.audit()
      AuthStub.authorise()

      val responsePayload: JsValue = Json.toJson[Error](InternalServerError)

      ExternalGuidanceStub.timescales(Status.INTERNAL_SERVER_ERROR, responsePayload)

      val request = buildRequest("/timescales")

      val response: WSResponse = await(request.get())

      response.status shouldBe Status.INTERNAL_SERVER_ERROR
    }

    "return an internal server error response when the external guidance service returns a bad request error" in {

      AuditStub.audit()
      AuthStub.authorise()

      val responsePayload: JsValue = Json.toJson[Error](BadRequestError)

      ExternalGuidanceStub.timescales(Status.BAD_REQUEST, responsePayload)

      val request = buildRequest("/timescales")

      val response: WSResponse = await(request.get())

      response.status shouldBe Status.BAD_REQUEST
    }

    "return an UNAUTHORIZED response for an unauthorized user" in {

      AuditStub.audit()
      AuthStub.unauthorised()

      ExternalGuidanceStub.timescales(Status.OK, timescaleResponseJson)

      val request: WSRequest = buildRequest("/timescales")

      val response: WSResponse = await(request.get())

      response.status shouldBe Status.UNAUTHORIZED
    }

  }

}
