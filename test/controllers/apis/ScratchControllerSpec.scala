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

import java.util.UUID.randomUUID

import base.BaseSpec
import mocks.{MockAppConfig, MockScratchService}
import models.ScratchResponse
import models.errors.{Error, ProcessError, InternalServerError, InvalidProcessError}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

import scala.concurrent.Future

class ScratchControllerSpec extends BaseSpec with GuiceOneAppPerSuite with MockScratchService {

  private val fakeRequest = FakeRequest("OPTIONS", "/")

  private val controller = new ScratchController(MockAppConfig, mockScratchService, stubMessagesControllerComponents())

  private val dummyProcess: JsValue = Json.parse(
    """|{
       | "processId": "10"
       |}""".stripMargin
  )

  private val fakeRequestWithBody: FakeRequest[JsValue] = FakeRequest("POST", "/").withBody(dummyProcess)

  private val uuid: String = randomUUID().toString

  private val locationUrlPrefix: String = "/guidance-review/scratch"

  "POST /scratch" should {

    "return 201" in {

      MockScratchService
        .scratchProcess(dummyProcess)
        .returns(Future.successful(Right(ScratchResponse(uuid))))

      val result = {
        controller.submitScratchProcess()(fakeRequestWithBody)
      }

      status(result) shouldBe Status.CREATED

    }

    "return process location in request header" in {

      MockScratchService
        .scratchProcess(dummyProcess)
        .returns(Future.successful(Right(ScratchResponse(uuid))))

      val result = {
        controller.submitScratchProcess()(fakeRequestWithBody)
      }

      val location: Option[String] = header("location", result)

      location match {
        case Some(location) => location shouldBe s"$locationUrlPrefix/$uuid"
        case None => fail("The location header is not defined")
      }

    }

    "return JSON" in {

      MockScratchService
        .scratchProcess(dummyProcess)
        .returns(Future.successful(Right(ScratchResponse(uuid))))

      val result = controller.submitScratchProcess()(fakeRequestWithBody)

      contentType(result) shouldBe Some("application/json")

      val jsValue: JsValue = Json.parse(contentAsString(result))

      val actualResponse: ScratchResponse = jsValue.as[ScratchResponse]

      actualResponse.id shouldBe uuid
    }

    "Handle an error raised owing to an invalid process being submitted" in {

      MockScratchService
        .scratchProcess(dummyProcess)
        .returns(Future.successful(Left(InvalidProcessError)))

      val result = controller.submitScratchProcess()(fakeRequestWithBody)

      status(result) shouldBe Status.BAD_REQUEST

      val jsValue: JsValue = Json.parse(contentAsString(result))

      val actualError: Error = jsValue.as[Error]

      actualError.code shouldBe InvalidProcessError.code
      actualError.message shouldBe InvalidProcessError.message
    }

    "Handle an error raised owing to a valid process but invalid guidance being submitted" in {

      val processErrors = List(ProcessError("An invalid stanza", "start"))
      MockScratchService
        .scratchProcess(dummyProcess)
        .returns(Future.successful(Left(Error(Error.UnprocessableEntity, processErrors))))

      val result = controller.submitScratchProcess()(fakeRequestWithBody)

      status(result) shouldBe Status.UNPROCESSABLE_ENTITY

      val jsValue: JsValue = Json.parse(contentAsString(result))

      val actualError: Error = jsValue.as[Error]

      actualError.code shouldBe Error.UnprocessableEntity
      actualError.messages shouldBe Some(processErrors)
    }


    "Handle an internal server error" in {

      MockScratchService
        .scratchProcess(dummyProcess)
        .returns(Future.successful(Left(InternalServerError)))

      val result = controller.submitScratchProcess()(fakeRequestWithBody)

      status(result) shouldBe Status.INTERNAL_SERVER_ERROR

      val jsValue: JsValue = Json.parse(contentAsString(result))

      val actualError: Error = jsValue.as[Error]

      actualError.code shouldBe InternalServerError.code
      actualError.message shouldBe InternalServerError.message
    }

  }

  "OPTIONS /scratch" should {
    "return 200" in {
      val result = controller.scratchProcessOptions()(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return CORS headers" in {
      val expectedHeaders = Map(
        "Access-Control-Allow-Origin" -> "*",
        "Access-Control-Allow-Headers" -> "*",
        "Access-Control-Allow-Methods" -> "POST, OPTIONS"
      )
      val result = controller.scratchProcessOptions()(fakeRequest)
      expectedHeaders.foreach { header =>
        headers(result) should contain(header)
      }
    }

  }

}
