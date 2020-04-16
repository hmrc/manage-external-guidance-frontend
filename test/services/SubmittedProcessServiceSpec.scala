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

import base.BaseSpec
import mocks.MockSubmittedProcessConnector
import models.errors.InternalServerError
import models.{RequestOutcome, SaveSubmittedProcessResponse}
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class SubmittedProcessServiceSpec extends BaseSpec {

  private trait Test extends MockSubmittedProcessConnector {

    implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

    lazy val submittedProcessService: SubmittedProcessService = new SubmittedProcessService(mockConnector)

    val processId: String = "abc12345"
    val dummyProcess: JsValue = Json.obj("meta" -> Json.obj("id"-> processId))
  }

  "The submittedProcess service" should {

    "Return an instance of the class SaveSubmittedProcessResponse after a successful call by the connector" in new Test {

      MockSubmittedProcessConnector
        .makeAvailableForApproval(dummyProcess)
        .returns(Future.successful(Right(SaveSubmittedProcessResponse(processId))))

      val result: Future[RequestOutcome[SaveSubmittedProcessResponse]] = submittedProcessService.saveForApproval(dummyProcess)

      result.onComplete {
        case Success(response) =>
          response match {
            case Right(response) => response.id shouldBe processId
            case Left(error) => fail(s"Unexpected error returned by submittedProcess connector : ${error.toString}")
          }
        case Failure(exception) => fail(s"Future onComplete returned unexpected error : ${exception.getMessage}")
      }

    }

    "Return an error after an unsuccessful call by the connector" in new Test {

      MockSubmittedProcessConnector
        .makeAvailableForApproval(dummyProcess)
        .returns(Future.successful(Left(InternalServerError)))

      val result: Future[RequestOutcome[SaveSubmittedProcessResponse]] = submittedProcessService.saveForApproval(dummyProcess)

      result.onComplete {
        case Success(response) =>
          response match {
            case Right(_) => fail("Submission response returned when an error was expected")
            case Left(error) => error shouldBe InternalServerError
          }
        case Failure(exception) => fail(s"Future onComplete returned unexpected error : ${exception.getMessage}")
      }
    }
  }
}
