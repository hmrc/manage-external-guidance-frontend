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

package services

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HeaderCarrier
import models.RequestOutcome
import models.errors.InternalServerError
import base.BaseSpec
import mocks.MockTimescalesConnector

import scala.util.{Failure, Success}

class TimescalesServiceSpec extends BaseSpec {

  trait Test extends MockTimescalesConnector {
    implicit val headerCarrier: HeaderCarrier = HeaderCarrier()
    lazy val timescalesService: TimescalesService = new TimescalesService(mockTimescalesConnector)
    val dummyTimescales: JsValue = Json.parse("""{"TimescaleID": 10}""")
  }

  "The Timescales service" should {

    "Return a Unit response after a successful call by the connector" in new Test {

      MockTimescalesConnector
        .submitTimescales(dummyTimescales)
        .returns(Future.successful(Right(())))

      val result: Future[RequestOutcome[Unit]] = timescalesService.submitTimescales(dummyTimescales)

      result.onComplete {
        case Success(response) =>
          response match {
            case Right(()) =>  succeed
            case Left(error) => fail(s"Unexpected error returned by timescales connector : ${error.toString}")
          }
        case Failure(exception) => fail(s"Future onComplete returned unexpected error : ${exception.getMessage}")
      }

    }

    "Return an error after an unsuccessful call by the connector" in new Test {

      MockTimescalesConnector
        .submitTimescales(dummyTimescales)
        .returns(Future.successful(Left(InternalServerError)))

      val result: Future[RequestOutcome[Unit]] = timescalesService.submitTimescales(dummyTimescales)

      result.onComplete {
        case Success(response) =>
          response match {
            case Right(()) => fail("Success response returned when an error was expected")
            case Left(error) => error shouldBe InternalServerError
          }

        case Failure(exception) => fail(s"Future onComplete returned unexpected error : ${exception.getMessage}")
      }
    }
  }
}
