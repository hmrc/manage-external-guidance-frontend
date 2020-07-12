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

import models.{PageReviewDetail, PageReviewStatus, YesNoAnswer}
import play.api.http.Status
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import stubs.{AuditStub, AuthStub, ExternalGuidanceStub}
import support.IntegrationSpec

class TwoEyePageReviewControllerISpec extends IntegrationSpec {

  private val baseReviewUrl = "/2i-page-review"
  
  "GET /2i-page-review/id/pageUrl" when {

    "user is authorised" should {

      "return OK (200)" in {
        val dataReturned = PageReviewDetail("oct90005", "pageUrl", "title", Some(YesNoAnswer.Yes), PageReviewStatus.NotStarted)

        ExternalGuidanceStub.approval2iPageReview(Status.OK, Json.toJson(dataReturned))

        AuditStub.audit()
        AuthStub.authorise()

        val request: WSRequest = buildRequest(s"$baseReviewUrl/oct90005/pageUrl")
        val response: WSResponse = await(request.get())

        response.status shouldBe Status.OK

      }
    }

    "user not authorised" should {

      "return UNAUTHORIZED" in {

        AuditStub.audit()
        AuthStub.unauthorised()

        val request: WSRequest = buildRequest(s"$baseReviewUrl/oct90005/pageUrl")
        val response: WSResponse = await(request.get())

        response.status shouldBe Status.UNAUTHORIZED

      }
    }
  }

  "POST /2i-page-review/id/pageUrl?title=Title" when {

    "user is authorised" when {

      "user selects a valid selection" should {

        "receive a redirect" in {

          AuditStub.audit()
          AuthStub.authorise()

          ExternalGuidanceStub.approval2iPageReviewComplete(Status.NO_CONTENT, Json.parse("{}"))

          val request: WSRequest = buildRequest(s"$baseReviewUrl/oct90005/pageUrl?title=Title")
          val response: WSResponse = await(request.post(Json.obj("answer" -> YesNoAnswer.Yes.toString, "title" -> "Title")))
          response.status shouldBe Status.SEE_OTHER
        }
      }

      "user enters an invalid selection" should {

        "return a bad request" in {

          AuditStub.audit()
          AuthStub.authorise()

          ExternalGuidanceStub.approval2iReviewComplete(Status.NO_CONTENT, Json.parse("{}"))

          val request: WSRequest = buildRequest(s"$baseReviewUrl/oct90005/pageUrl")
          val response: WSResponse = await(request.post(Json.obj("answer" -> "")))
          response.status shouldBe Status.BAD_REQUEST
        }
      }
    }
    "user not authorised" should {

      "return UNAUTHORIZED" in {

        AuditStub.audit()
        AuthStub.unauthorised()

        val request: WSRequest = buildRequest(s"$baseReviewUrl/oct90005/pageUrl?title=Title")
        val response: WSResponse = await(request.post(Json.obj("answer" -> YesNoAnswer.Yes.toString, "title" -> "Title")))
        response.status shouldBe Status.UNAUTHORIZED

      }
    }
  }
}
