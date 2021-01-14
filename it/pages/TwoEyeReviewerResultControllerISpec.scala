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

package pages

import models.ApprovalStatus
import models.audit.AuditInfo
import play.api.http.Status
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import stubs.{AuditStub, AuthStub, ExternalGuidanceStub}
import support.IntegrationSpec

class TwoEyeReviewerResultControllerISpec extends IntegrationSpec {

  "GET /2i-result/id" when {

    "user is authorised" should {

      "return OK (200)" in {

        AuditStub.audit()
        AuthStub.authorise()
        ExternalGuidanceStub.approval2iReviewCheck(Status.NO_CONTENT, Json.parse("{}"))

        val request: WSRequest = buildRequest("/2i-result/oct90005")
        val response: WSResponse = await(request.get())

        response.status shouldBe Status.OK

      }
    }

    "user not authorised" should {

      "return UNAUTHORIZED" in {

        AuditStub.audit()
        AuthStub.unauthorised()

        val request: WSRequest = buildRequest("/2i-result/oct90005")
        val response: WSResponse = await(request.get())

        response.status shouldBe Status.UNAUTHORIZED

      }
    }
  }

  "POST /2i-result/id" when {

    "user is authorised" when {
      "user selects to send guidance to designer" should {
        "receive a confirmation page" in {
          AuditStub.audit()
          AuthStub.authorise()
          val auditInfo: AuditInfo = AuditInfo("pid", "oct90005", "title", 1, "author", 2, 2)
          ExternalGuidanceStub.approval2iReviewComplete(Status.OK, Json.toJsObject(auditInfo))

          val request: WSRequest = buildRequest("/2i-result/oct90005")
          val response: WSResponse = await(request.post(Json.obj("value" -> ApprovalStatus.Complete.toString)))
          response.status shouldBe Status.OK
        }
      }
      "user selects to publish guidance" should {
        "receive a confirmation page" in {
          AuditStub.audit()
          AuthStub.authorise()
          val auditInfo: AuditInfo = AuditInfo("pid", "oct90005", "title", 1, "author", 2, 2)
          ExternalGuidanceStub.approval2iReviewComplete(Status.OK, Json.toJsObject(auditInfo))

          val request: WSRequest = buildRequest("/2i-result/oct90005")
          val response: WSResponse = await(request.post(Json.obj("value" -> ApprovalStatus.Published.toString)))
          response.status shouldBe Status.OK
        }
      }

      "user enters an invalid selection" should {

        "return a bad request" in {

          AuditStub.audit()
          AuthStub.authorise()

          ExternalGuidanceStub.approval2iReviewComplete(Status.NO_CONTENT, Json.parse("{}"))

          val request: WSRequest = buildRequest("/2i-result/oct90005")
          val response: WSResponse = await(request.post(Json.obj("value" -> ApprovalStatus.Submitted.toString)))
          response.status shouldBe Status.BAD_REQUEST
        }
      }
    }
    "user not authorised" should {

      "return UNAUTHORIZED" in {

        AuditStub.audit()
        AuthStub.unauthorised()

        val request: WSRequest = buildRequest("/2i-result/oct90005")
        val response: WSResponse = await(request.post(Json.obj("value" -> ApprovalStatus.Complete.toString)))
        response.status shouldBe Status.UNAUTHORIZED

      }
    }
  }
}
