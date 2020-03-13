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

package services

import scala.concurrent.ExecutionContext.Implicits.global
import java.util.UUID.randomUUID

import scala.concurrent.Future
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HeaderCarrier
import models.{RequestOutcome, ScratchProcessSubmissionResponse}
import models.errors.InternalServerError
import base.BaseSpec
import mocks.MockGuidanceConnector

import scala.util.{Failure, Success}

class GuidanceServiceSpec extends BaseSpec {

  private trait Test extends MockGuidanceConnector {

    implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

    lazy val guidanceService: GuidanceService = new GuidanceService(mockGuidanceConnector)

    val dummyProcess: JsValue = Json.parse(
      """|{
         | "processId": "11"
         |}""".stripMargin
    )

    val uuid: String = randomUUID().toString
  }

  "The guidance service" should {

    "Return an instance of the class ScratchProcessSubmissionResponse after a successful call by the connector" in new Test {

      MockGuidanceConnector
        .submitScratchProcess(dummyProcess)
        .returns(Future.successful(Right(ScratchProcessSubmissionResponse(uuid))))

      val result: Future[RequestOutcome[ScratchProcessSubmissionResponse]] = guidanceService.scratchProcess(dummyProcess)

      result.onComplete {
        case Success(response) => {
          response match {
            case Right(scratchProcessSubmissionResponse) => scratchProcessSubmissionResponse.id shouldBe uuid
            case Left(error) => fail(s"Unexpected error returned by guidance connector : ${error.toString}")
          }
        }
        case Failure(exception) => fail(s"Future onComplete returned unexpected error : ${exception.getMessage}")
      }

    }

    "Return an error after an unsuccessful call by the connector" in new Test {

      MockGuidanceConnector
        .submitScratchProcess(dummyProcess)
        .returns(Future.successful(Left(InternalServerError)))

      val result: Future[RequestOutcome[ScratchProcessSubmissionResponse]] = guidanceService.scratchProcess(dummyProcess)

      result.onComplete {
        case Success(response) => {
          response match {
            case Right(scratchProcessSubmissionResponse) => fail("Submission response returned when an error was expected")
            case Left(error) => error shouldBe InternalServerError
          }
        }
        case Failure(exception) => fail(s"Future onComplete returned unexpected error : ${exception.getMessage}")
      }
    }
  }
}