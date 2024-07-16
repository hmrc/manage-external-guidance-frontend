/*
 * Copyright 2024 HM Revenue & Customs
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
import mocks.MockAppConfig
import models.errors.InternalServerError
import models.{RequestOutcome, ApprovalResponse, ProcessSummary}
import play.api.libs.json.{JsValue, Json}
import java.time.ZonedDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.hmrc.http.HttpReads
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import scala.concurrent.ExecutionContext

class ApprovalConnectorSpec extends BaseSpec {

  private trait Test extends ConnectorTest {
    val connector: ApprovalConnector = new ApprovalConnector(mockHttpClient, MockAppConfig)

    val id: String = "Oct90005"
    val dummyProcess: JsValue = Json.obj("processId" -> id)
    val processCode: String = "code"
    val process: JsValue = Json.obj()
    val processSummary: ProcessSummary = ProcessSummary(id, processCode, 1, "author", None, ZonedDateTime.now, "actionedby", "Status")
  }

  "ApprovalConnector" should {

    "Return a list of process summaries" in new Test {

      when(requestBuilder.execute[RequestOutcome[List[ProcessSummary]]](any[HttpReads[RequestOutcome[List[ProcessSummary]]]], any[ExecutionContext]))
        .thenReturn(Future.successful(Right(List(processSummary))))

      val response: RequestOutcome[List[ProcessSummary]] =
        await(connector.summaries)

      response shouldBe Right(List(processSummary))
    }

    "Return a published process json by process code" in new Test {

      when(requestBuilder.execute[RequestOutcome[JsValue]](any[HttpReads[RequestOutcome[JsValue]]], any[ExecutionContext]))
        .thenReturn(Future.successful(Right(process)))

      val response: RequestOutcome[JsValue] =
        await(connector.getApprovalByProcessCode(processCode))

      response shouldBe Right(process)
    }
  }

  "Calling method submitFor2iReview with a dummy process" should {

    "Return an instance of the class ApprovalResponse for a successful call" in new Test {

      when(requestBuilder.execute[RequestOutcome[ApprovalResponse]](any[HttpReads[RequestOutcome[ApprovalResponse]]], any[ExecutionContext]))
        .thenReturn(Future.successful(Right(ApprovalResponse(id))))

      val response: RequestOutcome[ApprovalResponse] =
        await(connector.submitFor2iReview(dummyProcess))

      response shouldBe Right(ApprovalResponse(id))
    }

    "Return an instance of an error class when an error occurs" in new Test {

      when(requestBuilder.execute[RequestOutcome[ApprovalResponse]](any[HttpReads[RequestOutcome[ApprovalResponse]]], any[ExecutionContext]))
        .thenReturn(Future.successful(Left(InternalServerError)))

      val response: RequestOutcome[ApprovalResponse] =
        await(connector.submitFor2iReview(dummyProcess))

      response shouldBe Left(InternalServerError)
    }
  }

  "Calling method submitForFactCheck with a dummy process" should {

    "Return an instance of the class ApprovalResponse for a successful call" in new Test {

      when(requestBuilder.execute[RequestOutcome[ApprovalResponse]](any[HttpReads[RequestOutcome[ApprovalResponse]]], any[ExecutionContext]))
        .thenReturn(Future.successful(Right(ApprovalResponse(id))))

      val response: RequestOutcome[ApprovalResponse] =
        await(connector.submitForFactCheck(dummyProcess))

      response shouldBe Right(ApprovalResponse(id))
    }

    "Return an instance of an error class when an error occurs" in new Test {

      when(requestBuilder.execute[RequestOutcome[ApprovalResponse]](any[HttpReads[RequestOutcome[ApprovalResponse]]], any[ExecutionContext]))
        .thenReturn(Future.successful(Left(InternalServerError)))

      val response: RequestOutcome[ApprovalResponse] =
        await(connector.submitForFactCheck(dummyProcess))

      response shouldBe Left(InternalServerError)
    }
  }

}
