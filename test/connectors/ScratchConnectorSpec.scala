/*
 * Copyright 2023 HM Revenue & Customs
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

import java.util.UUID.randomUUID

import base.BaseSpec
import mocks.{MockAppConfig, MockHttpClientV2}
import models.errors.InternalServerError
import models.{RequestOutcome, ScratchResponse}
import play.api.libs.json.{JsValue, Json}
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ScratchConnectorSpec extends BaseSpec {

  private trait Test extends MockHttpClientV2 with FutureAwaits with DefaultAwaitTimeout {

    implicit val hc: HeaderCarrier = HeaderCarrier()

    val scratchConnector: ScratchConnector = new ScratchConnector(mockHttpClientV2, MockAppConfig)
    val endpoint: String = MockAppConfig.externalGuidanceBaseUrl + "/external-guidance/scratch"

    val dummyProcess: JsValue = Json.parse(
      """|{
         | "processId": "12"
         |}""".stripMargin
    )

    val id: String = randomUUID().toString
  }

  "Calling method submitScratchProcess with a dummy process" should {

    "Return an instance of the class ScratchResponse for a successful call" in new Test {

      MockedHttpClientV2
        .post(endpoint, dummyProcess)
        .returns(Future.successful(Right(ScratchResponse(id))))

      val response: RequestOutcome[ScratchResponse] =
        await(scratchConnector.submitScratchProcess(dummyProcess))

      response shouldBe Right(ScratchResponse(id))
    }

    "Return an instance of an error class when an error occurs" in new Test {

      MockedHttpClientV2
        .post(endpoint, dummyProcess)
        .returns(Future.successful(Left(InternalServerError)))

      val response: RequestOutcome[ScratchResponse] =
        await(scratchConnector.submitScratchProcess(dummyProcess))

      response shouldBe Left(InternalServerError)
    }
  }
}
