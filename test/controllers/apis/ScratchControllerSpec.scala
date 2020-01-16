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

import config.AppConfig
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Configuration, Environment, _}
import uk.gov.hmrc.play.bootstrap.config.{RunMode, ServicesConfig}

class ScratchControllerSpec extends WordSpec with Matchers with GuiceOneAppPerSuite {
  private val fakeRequest = FakeRequest("OPTIONS", "/")
  private val fakeRequestWithBody = FakeRequest("POST", "/").withBody(Json.obj())

  private val env = Environment.simple()
  private val configuration = Configuration.load(env)

  private val serviceConfig = new ServicesConfig(configuration, new RunMode(configuration, Mode.Dev))
  private val appConfig = new AppConfig(configuration, serviceConfig)

  private val controller = new ScratchController(appConfig, stubControllerComponents())

  "POST /scratch" should {
    "return 201" in {
      val result = controller.scratchProcess()(fakeRequestWithBody)
      status(result) shouldBe Status.CREATED
    }

    "return JSON" in {
      val result = controller.scratchProcess()(fakeRequestWithBody)
      contentType(result) shouldBe Some("application/json")
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
