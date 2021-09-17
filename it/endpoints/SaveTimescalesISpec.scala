/*
 * Copyright 2021 HM Revenue & Customs
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

package endpoints

import play.api.http.Status
import play.api.libs.ws.WSResponse
import play.api.libs.json._
import models.errors.{InternalServerError, InvalidProcessError}
import stubs.{AuditStub, AuthStub, ExternalGuidanceStub}
import support.IntegrationSpec

class SaveTimescalesISpec extends IntegrationSpec {

  val validTimescalesJson = Json.obj("TimescaleID" -> 54)

  "calling the timescales POST endpoint" should {
    "return an NO_CONTENT response" in {

      // AuditStub.audit()
      // AuthStub.authorise()

      ExternalGuidanceStub.saveTimescales(Status.NO_CONTENT, JsNull)

      val request = buildRequest("/timescales")
      val response: WSResponse = {
        AuditStub.audit()
        AuthStub.authorise()
        await(request.post(validTimescalesJson))
      }
      response.status shouldBe Status.NO_CONTENT
    }

    "return a BAD_REQUEST response when the external guidance microservice rejects with BadRequest" in {

      // AuditStub.audit()
      // AuthStub.authorise()

      val responsePayload: JsValue = Json.toJson(InvalidProcessError)
      ExternalGuidanceStub.saveTimescales(Status.BAD_REQUEST, responsePayload)

      val request = buildRequest("/timescales")
      val response: WSResponse = {
        AuditStub.audit()
        AuthStub.authorise()
        await(request.post(validTimescalesJson))
      }
      response.status shouldBe Status.BAD_REQUEST
    }

    "return an INTERNAL_SERVER_ERROR response when the external guidance microservice returns an internal server error" in {

      // AuditStub.audit()
      // AuthStub.authorise()

      val responsePayload: JsValue = Json.toJson(InternalServerError)
      ExternalGuidanceStub.saveTimescales(Status.INTERNAL_SERVER_ERROR, responsePayload)

      val request = buildRequest("/timescales")
      val response: WSResponse = {
        AuditStub.audit()
        AuthStub.authorise()
        await(request.post(Json.obj("message" -> "hi")))
      }
      response.status shouldBe Status.INTERNAL_SERVER_ERROR
    }
  }

  "calling the scratch OPTIONS endpoint" should {
    "return an OK response" in {
      AuditStub.audit()
      val request = buildRequest("/timescales")
      val response: WSResponse = await(request.options())
      response.status shouldBe Status.OK
    }
  }

}
