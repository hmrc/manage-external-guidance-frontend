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
import scala.concurrent.Future
import play.api.{Configuration, Environment, _}
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.config.{RunMode, ServicesConfig}
import config.AppConfig
import models.ScratchProcessSubmissionResponse
import models.errors.Error
import base.BaseSpec

import scala.util.{Failure, Success}

class GuidanceConnectorSpec extends BaseSpec {

  "Calling method submitScratchProcess with a dummy process" should {

    "Return an instance of the class ScratchProcessSubmissionResponse" in {

      val env = Environment.simple()
      val configuration = Configuration.load(env)

      val serviceConfig = new ServicesConfig(configuration, new RunMode(configuration, Mode.Dev))
      val appConfig = new AppConfig(configuration, serviceConfig)

      val hc: HeaderCarrier = HeaderCarrier()

      val dummyProcess: JsValue = Json.parse(
        """|{
           | "processId": "12"
           |}""".stripMargin
      )

      val guidanceConnector: GuidanceConnector = new GuidanceConnector(appConfig)

      val expectedResponse = guidanceConnector.stubbedResponse

      val response: Future[Either[Error, ScratchProcessSubmissionResponse]] = guidanceConnector
        .submitScratchProcess(dummyProcess)(implicitly, hc)

      response.onComplete {
        case Success(actualResponse) => {
          actualResponse match {
            case Right(scratchProcessSubmissionResponse) => scratchProcessSubmissionResponse shouldBe expectedResponse
            case Left(error) => fail(s"Unexpected error returned by guidance connector : ${error.toString}")
          }
        }
        case Failure(exception) => fail(s"Future onComplete returned unexpected error : ${exception.getMessage}")
      }

    }
  }

}
