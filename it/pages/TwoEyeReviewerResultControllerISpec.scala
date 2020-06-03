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

import java.time.LocalDate
import java.util.UUID

import models.{ApprovalProcessReview, ApprovalStatus, PageReview, PageReviewStatus}
import play.api.http.Status
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import stubs.{AuditStub, AuthStub, ExternalGuidanceStub}
import support.IntegrationSpec

class TwoEyeReviewerResultControllerISpec extends IntegrationSpec {

  val processReviewInfo: ApprovalProcessReview =
    ApprovalProcessReview(
      UUID.randomUUID().toString,
      "oct90005",
      "Telling HMRC about extra income",
      LocalDate.of(2020, 5, 10),
      List(
        PageReview("id1", "how-did-you-earn-extra-income", PageReviewStatus.NotStarted),
        PageReview("id2", "sold-goods-or-services/did-you-only-sell-personal-possessions", PageReviewStatus.NotStarted),
        PageReview("id3", "sold-goods-or-services/have-you-made-a-profit-of-6000-or-more", PageReviewStatus.NotStarted),
        PageReview("id4", "sold-goods-or-services/have-you-made-1000-or-more", PageReviewStatus.NotStarted),
        PageReview("id5", "sold-goods-or-services/you-do-not-need-to-tell-hmrc", PageReviewStatus.NotStarted),
        PageReview("id6", "rent-a-property/do-you-receive-any-income", PageReviewStatus.NotStarted),
        PageReview("id7", "rent-a-property/have-you-rented-out-a-room", PageReviewStatus.NotStarted)
      )
    )

  "GET /2i-result/id" when {

    "user is authorised" should {

      "return OK (200)" in {

        AuditStub.audit()
        AuthStub.authorise()

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

      "user selects a valid selection" should {

        "receive a redirect" in {

          AuditStub.audit()
          AuthStub.authorise()

          ExternalGuidanceStub.approval2iReviewComplete(Status.NO_CONTENT, Json.parse("{}"))

          val request: WSRequest = buildRequest("/2i-result/oct90005")
          val response: WSResponse = await(request.post(Json.obj("value" -> ApprovalStatus.WithDesignerForUpdate.toString)))
          response.status shouldBe Status.SEE_OTHER
          println(response)
        }
      }

      "user enters an invalid selection" should {

        "return a bad request" in {

          AuditStub.audit()
          AuthStub.authorise()

          ExternalGuidanceStub.approval2iReviewComplete(Status.NO_CONTENT, Json.parse("{}"))

          val request: WSRequest = buildRequest("/2i-result/oct90005")
          val response: WSResponse = await(request.post(Json.obj("value" -> ApprovalStatus.SubmittedFor2iReview.toString)))
          response.status shouldBe Status.BAD_REQUEST
        }
      }
    }
    "user not authorised" should {

      "return UNAUTHORIZED" in {

        AuditStub.audit()
        AuthStub.unauthorised()

        val request: WSRequest = buildRequest("/2i-result/oct90005")
        val response: WSResponse = await(request.post(Json.obj("value" -> ApprovalStatus.WithDesignerForUpdate.toString)))
        response.status shouldBe Status.UNAUTHORIZED

      }
    }
  }
}
