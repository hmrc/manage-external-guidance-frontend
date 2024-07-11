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

import base.BaseSpec
import mocks.{MockAppConfig, MockHttpClientV2}
import models.errors.InternalServerError
import models.{RequestOutcome, ApprovalResponse, ProcessSummary}
import play.api.libs.json.{JsValue, Json}
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.http.HeaderCarrier
import java.time.ZonedDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ApprovalConnectorSpec extends BaseSpec {

  private trait Test extends MockHttpClientV2 with FutureAwaits with DefaultAwaitTimeout {

    implicit val hc: HeaderCarrier = HeaderCarrier()

    val connector: ApprovalConnector = new ApprovalConnector(mockHttpClientV2, MockAppConfig)

    val id: String = "Oct90005"
    val dummyProcess: JsValue = Json.obj("processId" -> id)
    val now = ZonedDateTime.now
    val processCode: String = "code"
    val process = Json.obj()
    val processSummary = ProcessSummary(id, processCode, 1, "author", None, now, "actionedby", "Status")

  }

  "ApprovalConnector" should {

    "Return a list of process summaries" in new Test {

      MockedHttpClientV2
        .get(MockAppConfig.externalGuidanceBaseUrl + s"/external-guidance/approval/list")
        .returns(Future.successful(Right(List(processSummary))))

      val response: RequestOutcome[List[ProcessSummary]] =
        await(connector.summaries)

      response shouldBe Right(List(processSummary))
    }

    "Return a published process json by process code" in new Test {

      MockedHttpClientV2
        .get(MockAppConfig.externalGuidanceBaseUrl + s"/external-guidance/approval/code/$processCode")
        .returns(Future.successful(Right(process)))

      val response: RequestOutcome[JsValue] =
        await(connector.getApprovalByProcessCode(processCode))

      response shouldBe Right(process)
    }
  }

  "Calling method submitFor2iReview with a dummy process" should {

    trait SubmitFor2iReviewTest extends Test {

      val endpoint: String = MockAppConfig.externalGuidanceBaseUrl + "/external-guidance/approval/2i-review"

    }
    "Return an instance of the class ApprovalResponse for a successful call" in new SubmitFor2iReviewTest {

      MockedHttpClientV2
        .post(endpoint, dummyProcess)
        .returns(Future.successful(Right(ApprovalResponse(id))))

      val response: RequestOutcome[ApprovalResponse] =
        await(connector.submitFor2iReview(dummyProcess))

      response shouldBe Right(ApprovalResponse(id))
    }

    "Return an instance of an error class when an error occurs" in new SubmitFor2iReviewTest {

      MockedHttpClientV2
        .post(endpoint, dummyProcess)
        .returns(Future.successful(Left(InternalServerError)))

      val response: RequestOutcome[ApprovalResponse] =
        await(connector.submitFor2iReview(dummyProcess))

      response shouldBe Left(InternalServerError)
    }
  }

  "Calling method submitForFactCheck with a dummy process" should {

    trait SubmitForFactCheckTest extends Test {

      val endpoint: String = MockAppConfig.externalGuidanceBaseUrl + "/external-guidance/approval/fact-check"

    }
    "Return an instance of the class ApprovalResponse for a successful call" in new SubmitForFactCheckTest {

      MockedHttpClientV2
        .post(endpoint, dummyProcess)
        .returns(Future.successful(Right(ApprovalResponse(id))))

      val response: RequestOutcome[ApprovalResponse] =
        await(connector.submitForFactCheck(dummyProcess))

      response shouldBe Right(ApprovalResponse(id))
    }

    "Return an instance of an error class when an error occurs" in new SubmitForFactCheckTest {

      MockedHttpClientV2
        .post(endpoint, dummyProcess)
        .returns(Future.successful(Left(InternalServerError)))

      val response: RequestOutcome[ApprovalResponse] =
        await(connector.submitForFactCheck(dummyProcess))

      response shouldBe Left(InternalServerError)
    }
  }

}
