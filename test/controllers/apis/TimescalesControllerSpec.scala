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

package controllers.apis

import base.BaseSpec
import mocks.MockTimescalesService
import models.errors.{Error, InternalServerError, ValidationError}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class TimescalesControllerSpec extends BaseSpec with GuiceOneAppPerSuite with MockTimescalesService {

  private val dummyTimescales: JsValue = Json.parse("""{"TimescaleID": 10}""")
  private val fakeRequest = FakeRequest("OPTIONS", "/")
  private val controller = new TimescalesController(mockTimescalesService, stubMessagesControllerComponents())
  private val fakeRequestWithBody: FakeRequest[JsValue] = FakeRequest("POST", "/").withBody(dummyTimescales)
  private val locationUrlPrefix: String = "/guidance-review/timescales"

  "POST /timescales" should {

    "return 204" in {

      MockTimescalesService
        .submitTimescales(dummyTimescales)
        .returns(Future.successful(Right(())))

      val result = controller.submitTimescales()(fakeRequestWithBody)

      status(result) shouldBe Status.NO_CONTENT
    }

    "return process location in request header" in {

      MockTimescalesService
        .submitTimescales(dummyTimescales)
        .returns(Future.successful(Right(())))

      val result = controller.submitTimescales()(fakeRequestWithBody)
      val location: Option[String] = header("location", result)

      location match {
        case Some(location) => location shouldBe s"$locationUrlPrefix"
        case None => fail("The location header is not defined")
      }

    }

    "Handle an error raised owing to an invalid timescales being submitted" in {

      MockTimescalesService
        .submitTimescales(dummyTimescales)
        .returns(Future.successful(Left(ValidationError)))

      val result = controller.submitTimescales()(fakeRequestWithBody)

      status(result) shouldBe Status.BAD_REQUEST

      val jsValue: JsValue = Json.parse(contentAsString(result))

      val actualError: Error = jsValue.as[Error]

      actualError.code shouldBe ValidationError.code
      actualError.message shouldBe ValidationError.message
    }

    "Handle an internal server error" in {

      MockTimescalesService
        .submitTimescales(dummyTimescales)
        .returns(Future.successful(Left(InternalServerError)))

      val result = controller.submitTimescales()(fakeRequestWithBody)

      status(result) shouldBe Status.INTERNAL_SERVER_ERROR

      val jsValue: JsValue = Json.parse(contentAsString(result))

      val actualError: Error = jsValue.as[Error]

      actualError.code shouldBe InternalServerError.code
      actualError.message shouldBe InternalServerError.message
    }

  }

  "OPTIONS /scratch" should {
    "return 200" in {
      val result = controller.timescaleOptions()(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return CORS headers" in {
      val expectedHeaders = Map(
        "Access-Control-Allow-Origin" -> "*",
        "Access-Control-Allow-Headers" -> "*",
        "Access-Control-Allow-Methods" -> "POST, OPTIONS"
      )
      val result = controller.timescaleOptions()(fakeRequest)
      expectedHeaders.foreach { header =>
        headers(result) should contain(header)
      }
    }

  }

}
