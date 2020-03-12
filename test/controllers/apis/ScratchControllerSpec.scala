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

import scala.concurrent.Future

import base.BaseSpec
import config.AppConfig
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.libs.json.{Json, JsValue}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Configuration, Environment, _}
import uk.gov.hmrc.play.bootstrap.config.{RunMode, ServicesConfig}
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents
import models.ScratchProcessSubmissionResponse
import models.errors.{Error, ExternalGuidanceServiceError, InternalServerError, InvalidProcessError}
import mocks.MockGuidanceService

class ScratchControllerSpec extends BaseSpec with GuiceOneAppPerSuite with MockGuidanceService {

  private val fakeRequest = FakeRequest("OPTIONS", "/")

  private val env = Environment.simple()
  private val configuration = Configuration.load(env)

  private val serviceConfig = new ServicesConfig(configuration, new RunMode(configuration, Mode.Dev))
  private val appConfig = new AppConfig(configuration, serviceConfig)

  private val controller = new ScratchController(appConfig, mockGuidanceService, stubMessagesControllerComponents())

  private val dummyProcess: JsValue = Json.parse(
    """|{
       | "processId": "10"
       |}""".stripMargin
  )

  private val fakeRequestWithBody = FakeRequest("POST", "/").withBody(dummyProcess)

  private val uuid: String = randomUUID().toString

  private val locationUrlPrefix: String = "/guidance/scratch"

  "POST /scratch" should {

    "return 201" in {

      MockGuidanceService
        .scratchProcess(dummyProcess)
        .returns(Future.successful(Right(ScratchProcessSubmissionResponse(uuid))))

      val result = {
        controller.scratchProcess()(fakeRequestWithBody)
      }

      status(result) shouldBe Status.CREATED

    }

    "return process location in request header" in {

      MockGuidanceService
        .scratchProcess(dummyProcess)
        .returns(Future.successful(Right(ScratchProcessSubmissionResponse(uuid))))

      val result = {
        controller.scratchProcess()(fakeRequestWithBody)
      }

      val location: Option[String] = header("location", result)

      location match {
        case Some(location) => location shouldBe s"$locationUrlPrefix/$uuid"
        case None => fail("The location header is not defined")
      }

    }

    "return JSON" in {

      MockGuidanceService
        .scratchProcess(dummyProcess)
        .returns(Future.successful(Right(ScratchProcessSubmissionResponse(uuid))))

      val result = controller.scratchProcess()(fakeRequestWithBody)

      contentType(result) shouldBe Some("application/json")

      val jsValue: JsValue = Json.parse(contentAsString(result))

      val actualResponse: ScratchProcessSubmissionResponse = jsValue.as[ScratchProcessSubmissionResponse]

      actualResponse.id shouldBe uuid
    }

    "Handle an error raised owing to an invalid process being submitted" in {

      MockGuidanceService
        .scratchProcess(dummyProcess)
        .returns(Future.successful(Left(InvalidProcessError)))

      val result = controller.scratchProcess()(fakeRequestWithBody)

      status(result) shouldBe Status.BAD_REQUEST

      val jsValue: JsValue = Json.parse(contentAsString(result))

      val actualError: Error = jsValue.as[Error]

      actualError.code shouldBe InvalidProcessError.code
      actualError.message shouldBe InvalidProcessError.message
    }

    "Handle an error raised by the external guidance microservice" in {

      MockGuidanceService
        .scratchProcess(dummyProcess)
        .returns(Future.successful(Left(ExternalGuidanceServiceError)))

      val result = controller.scratchProcess()(fakeRequestWithBody)

      status(result) shouldBe Status.INTERNAL_SERVER_ERROR

      val jsValue: JsValue = Json.parse(contentAsString(result))

      val actualError: Error = jsValue.as[Error]

      actualError.code shouldBe ExternalGuidanceServiceError.code
      actualError.message shouldBe ExternalGuidanceServiceError.message
    }

    "Handle an internal server error" in {

      MockGuidanceService
        .scratchProcess(dummyProcess)
        .returns(Future.successful(Left(InternalServerError)))

      val result = controller.scratchProcess()(fakeRequestWithBody)

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
