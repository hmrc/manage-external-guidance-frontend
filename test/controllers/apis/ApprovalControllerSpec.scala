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

package controllers.apis

import base.BaseSpec
import mocks.{MockAppConfig, MockApprovalService}
import models.ApprovalResponse
import models.errors.{Error, InternalServerError, InvalidProcessError}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

import scala.concurrent.Future

class ApprovalControllerSpec extends BaseSpec with GuiceOneAppPerSuite with MockApprovalService {

  private val fakeRequest = FakeRequest("OPTIONS", "/")

  private val controller = new ApprovalController(MockAppConfig, mockApprovalService, stubMessagesControllerComponents())

  private val dummyProcess: JsValue = Json.parse(
    """|{
       | "processId": "oct09010"
       |}""".stripMargin
  )

  private val fakeRequestWithBody: FakeRequest[JsValue] = FakeRequest("POST", "/").withBody(dummyProcess)

  private val expectedId: String = "oct90001"

  "POST /approval" should {

    "return 201" in {

      MockApprovalService
        .submitForApproval(dummyProcess)
        .returns(Future.successful(Right(ApprovalResponse(expectedId))))

      val result = {
        controller.submitForApproval()(fakeRequestWithBody)
      }

      status(result) shouldBe Status.CREATED

    }

    "return JSON" in {

      MockApprovalService
        .submitForApproval(dummyProcess)
        .returns(Future.successful(Right(ApprovalResponse(expectedId))))

      val result = controller.submitForApproval()(fakeRequestWithBody)

      contentType(result) shouldBe Some("application/json")

      val jsValue: JsValue = Json.parse(contentAsString(result))

      val actualResponse: ApprovalResponse = jsValue.as[ApprovalResponse]

      actualResponse.id shouldBe expectedId
    }

    "Handle an error raised owing to an invalid process being submitted" in {

      MockApprovalService
        .submitForApproval(dummyProcess)
        .returns(Future.successful(Left(InvalidProcessError)))

      val result = controller.submitForApproval()(fakeRequestWithBody)

      status(result) shouldBe Status.BAD_REQUEST

      val jsValue: JsValue = Json.parse(contentAsString(result))

      val actualError: Error = jsValue.as[Error]

      actualError.code shouldBe InvalidProcessError.code
      actualError.message shouldBe InvalidProcessError.message
    }

    "Handle an internal server error" in {

      MockApprovalService
        .submitForApproval(dummyProcess)
        .returns(Future.successful(Left(InternalServerError)))

      val result = controller.submitForApproval()(fakeRequestWithBody)

      status(result) shouldBe Status.INTERNAL_SERVER_ERROR

      val jsValue: JsValue = Json.parse(contentAsString(result))

      val actualError: Error = jsValue.as[Error]

      actualError.code shouldBe InternalServerError.code
      actualError.message shouldBe InternalServerError.message
    }

  }

  "OPTIONS /approval" should {
    "return 200" in {
      val result = controller.options()(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return CORS headers" in {
      val expectedHeaders = Map(
        "Access-Control-Allow-Origin" -> "*",
        "Access-Control-Allow-Headers" -> "*",
        "Access-Control-Allow-Methods" -> "POST, OPTIONS"
      )
      val result = controller.options()(fakeRequest)
      expectedHeaders.foreach { header =>
        headers(result) should contain(header)
      }
    }

  }

}
