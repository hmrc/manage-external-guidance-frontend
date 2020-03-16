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

package connectors

import scala.concurrent.ExecutionContext.Implicits.global

import java.util.UUID.randomUUID

import scala.concurrent.Future
import play.api.{Configuration, Environment, _}
import play.api.http.{ContentTypes, HeaderNames}
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.config.{RunMode, ServicesConfig}
import config.AppConfig
import models.{RequestOutcome, ScratchProcessSubmissionResponse}
import models.errors.InternalServerError

import base.BaseSpec
import mocks.MockHttpClient
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}

class GuidanceConnectorSpec extends BaseSpec {

  private trait Test extends MockHttpClient with FutureAwaits with DefaultAwaitTimeout {

    val env = Environment.simple()
    val configuration = Configuration.load(env)

    val serviceConfig = new ServicesConfig(configuration, new RunMode(configuration, Mode.Dev))
    val appConfig = new AppConfig(configuration, serviceConfig)

    val hc: HeaderCarrier = HeaderCarrier()

    val guidanceConnector: GuidanceConnector = new GuidanceConnector(mockHttpClient, appConfig)

    val endpoint: String = appConfig.externalGuidanceScratchUrl

    val dummyProcess: JsValue = Json.parse(
      """|{
         | "processId": "12"
         |}""".stripMargin
    )

    val headers = Seq(HeaderNames.CONTENT_TYPE -> ContentTypes.JSON)

    val id: String = randomUUID().toString
  }

  "Calling method submitScratchProcess with a dummy process" should {

    "Return an instance of the class ScratchProcessSubmissionResponse for a successful call" in new Test {

      MockedHttpClient
        .post(endpoint, dummyProcess)
        .returns(Future.successful(Right(ScratchProcessSubmissionResponse(id))))

      val response: RequestOutcome[ScratchProcessSubmissionResponse] = await(
        guidanceConnector
          .submitScratchProcess(dummyProcess)(implicitly, hc)
      )

      response shouldBe Right(ScratchProcessSubmissionResponse(id))
    }

    "Return an instance of an error class when an error occurs" in new Test {

      MockedHttpClient
        .post(endpoint, dummyProcess)
        .returns(Future.successful(Left(InternalServerError)))

      val response: RequestOutcome[ScratchProcessSubmissionResponse] = await(
        guidanceConnector
          .submitScratchProcess(dummyProcess)(implicitly, hc)
      )

      response shouldBe Left(InternalServerError)
    }
  }

}
