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

import java.util.UUID.randomUUID

import base.BaseSpec
import mocks.{MockAppConfig, MockHttpClient}
import models.errors.InternalServerError
import models.{RequestOutcome, SaveScratchSubmissionResponse, SaveSubmittedProcessResponse}
import play.api.libs.json.{JsValue, Json}
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GuidanceConnectorSpec extends BaseSpec {

  private trait Test extends MockHttpClient with FutureAwaits with DefaultAwaitTimeout {

    implicit val hc: HeaderCarrier = HeaderCarrier()

    val guidanceConnector: GuidanceConnector = new GuidanceConnector(mockHttpClient, MockAppConfig)
    val endpoint: String = MockAppConfig.externalGuidanceBaseUrl + "/external-guidance/scratch"

    val dummyProcess: JsValue = Json.parse(
      """|{
         | "processId": "12"
         |}""".stripMargin
    )

    val id: String = randomUUID().toString
  }

  "Calling method submitScratchProcess with a dummy process" should {

    "Return an instance of the class ScratchProcessSubmissionResponse for a successful call" in new Test {

      MockedHttpClient
        .post(endpoint, dummyProcess)
        .returns(Future.successful(Right(SaveScratchSubmissionResponse(id))))

      val response: RequestOutcome[SaveScratchSubmissionResponse] =
        await(guidanceConnector.submitScratchProcess(dummyProcess))

      response shouldBe Right(SaveScratchSubmissionResponse(id))
    }

    "Return an instance of an error class when an error occurs" in new Test {

      MockedHttpClient
        .post(endpoint, dummyProcess)
        .returns(Future.successful(Left(InternalServerError)))

      val response: RequestOutcome[SaveScratchSubmissionResponse] =
        await(guidanceConnector.submitScratchProcess(dummyProcess))

      response shouldBe Left(InternalServerError)
    }
  }
  private trait SubmitForApprovalTest extends MockHttpClient with FutureAwaits with DefaultAwaitTimeout {

    implicit val hc: HeaderCarrier = HeaderCarrier()

    val guidanceConnector: GuidanceConnector = new GuidanceConnector(mockHttpClient, MockAppConfig)
    val endpoint: String = MockAppConfig.externalGuidanceBaseUrl + "/external-guidance/submitted"

    val id: String = "Oct90005"
    val dummyProcess: JsValue = Json.obj("processId" -> id)

  }

  "Calling method makeAvailableForApproval with a dummy process" should {

    "Return an instance of the class SaveSubmittedProcessResponse for a successful call" in new SubmitForApprovalTest {

      MockedHttpClient
        .post(endpoint, dummyProcess)
        .returns(Future.successful(Right(SaveSubmittedProcessResponse(id))))

      val response: RequestOutcome[SaveSubmittedProcessResponse] =
        await(guidanceConnector.makeAvailableForApproval(dummyProcess))

      response shouldBe Right(SaveSubmittedProcessResponse(id))
    }

    "Return an instance of an error class when an error occurs" in new SubmitForApprovalTest {

      MockedHttpClient
        .post(endpoint, dummyProcess)
        .returns(Future.successful(Left(InternalServerError)))

      val response: RequestOutcome[SaveSubmittedProcessResponse] =
        await(guidanceConnector.makeAvailableForApproval(dummyProcess))

      response shouldBe Left(InternalServerError)
    }
  }

}
