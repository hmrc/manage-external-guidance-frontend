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

import models.errors.{BadRequestError, InternalServerError, NotFoundError, StaleDataError}
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import stubs.{AuditStub, AuthStub, ExternalGuidanceStub}
import support.IntegrationSpec

class TwoEyeReviewerControllerISpec extends IntegrationSpec {

  private val successPayload: JsValue = Json.parse(
    """{
      |  "id":"oct90005",
      |  "ocelotId":"oct90005",
      |  "title":"Telling HMRC about extra income",
      |  "lastUpdated":"2020-05-10",
      |  "pages":[
      |    {
      |      "id":"id1",
      |      "title":"how-did-you-earn-extra-income",
      |      "status":"NotStarted"
      |    },
      |    {
      |      "id":"id2",
      |      "title":"sold-goods-or-services/did-you-only-sell-personal-possessions",
      |      "status":"NotStarted"
      |    },
      |    {
      |      "id":"id3",
      |      "title":"sold-goods-or-services/have-you-made-a-profit-of-6000-or-more",
      |      "status":"NotStarted"
      |    },
      |    {
      |      "id":"id4",
      |      "title":"sold-goods-or-services/have-you-made-1000-or-more",
      |      "status":"NotStarted"
      |    },
      |    {
      |      "id":"id5",
      |      "title":"sold-goods-or-services/you-do-not-need-to-tell-hmrc",
      |      "status":"NotStarted"}
      |    ,
      |    {
      |      "id":"id6",
      |      "title":"rent-a-property/do-you-receive-any-income",
      |      "status":"NotStarted"
      |    },
      |    {
      |      "id":"id7",
      |      "title":"rent-a-property/have-you-rented-out-a-room",
      |      "status":"NotStarted"
      |    }
      |  ]
      |}""".stripMargin
  )

  "Calling the approval 2i review endpoint with a valid process identifier" should {

    "return an Ok response" in {

      AuditStub.audit()
      AuthStub.authorise()

      ExternalGuidanceStub.approval2iReview(Status.OK, successPayload)

      val request: WSRequest = buildRequest("/2i-review/oct90005")

      val response: WSResponse = await(request.get())

      response.status shouldBe Status.OK
    }

    "return a not found response when the external guidance service cannot locate the requested process" in {

      AuditStub.audit()
      AuthStub.authorise()

      val responsePayload: JsValue = Json.toJson(NotFoundError)

      ExternalGuidanceStub.approval2iReview(Status.NOT_FOUND, responsePayload)

      val request = buildRequest("/2i-review/oct90005")

      val response: WSResponse = await(request.get())

      response.status shouldBe Status.NOT_FOUND
    }

    "return an internal server error response when an unexpected error is returned by the external guidance service" in {

      AuditStub.audit()
      AuthStub.authorise()

      val responsePayload: JsValue = Json.toJson(InternalServerError)

      ExternalGuidanceStub.approval2iReview(Status.INTERNAL_SERVER_ERROR, responsePayload)

      val request = buildRequest("/2i-review/oct90005")

      val response: WSResponse = await(request.get())

      response.status shouldBe Status.INTERNAL_SERVER_ERROR
    }

    "return a not found error response when the external guidance service returns a stale data error" in {

      AuditStub.audit()
      AuthStub.authorise()

      val responsePayload: JsValue = Json.toJson(StaleDataError)

      ExternalGuidanceStub.approval2iReview(Status.NOT_FOUND, responsePayload)

      val request = buildRequest("/2i-review/oct90005")

      val response: WSResponse = await(request.get())

      response.status shouldBe Status.NOT_FOUND
    }

    "return an internal server error response when the external guidance service returns a bad request error" in {

      AuditStub.audit()
      AuthStub.authorise()

      val responsePayload: JsValue = Json.toJson(BadRequestError)

      ExternalGuidanceStub.approval2iReview(Status.BAD_REQUEST, responsePayload)

      val request = buildRequest("/2i-review/oct90005")

      val response: WSResponse = await(request.get())

      response.status shouldBe Status.INTERNAL_SERVER_ERROR
    }

    "return an UNAUTHORIZED response for an unauthorized user" in {

      AuditStub.audit()
      AuthStub.unauthorised()

      ExternalGuidanceStub.approval2iReview(Status.OK, successPayload)

      val request: WSRequest = buildRequest("/2i-review/oct90005")

      val response: WSResponse = await(request.get())

      response.status shouldBe Status.UNAUTHORIZED
    }

  }

}
