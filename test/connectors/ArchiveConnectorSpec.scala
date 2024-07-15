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

import java.time.ZonedDateTime
import base.BaseSpec
import mocks.MockAppConfig
import models.errors.BadRequestError
import models.{ProcessSummary, RequestOutcome}
import play.api.http.Status
import uk.gov.hmrc.http.{HttpReads, HttpResponse, UpstreamErrorResponse}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when

class ArchiveConnectorSpec extends BaseSpec {

  private trait Test extends ConnectorTest {

    val connector: ArchiveConnector = new ArchiveConnector(mockHttpClient, MockAppConfig)

    val id: String = "Oct90005"
    val now = ZonedDateTime.now
    val processCode: String = "code"
    val process = Json.obj()
    val processSummary = ProcessSummary(id, processCode, 1, "author", None, now, "actionedby", "Status")
  }

  "Calling method archive with a dummy id" should {

    "Return true for a successful call" in new Test {
      when(requestBuilder.execute[HttpResponse](any[HttpReads[HttpResponse]], any[ExecutionContext]))
        .thenReturn(Future.successful(HttpResponse(Status.OK, "200")))

      val response: RequestOutcome[Boolean] = await(connector.archive(id))

      response shouldBe Right(true)
    }

    "Return an instance of an error class when an error occurs" in new Test {
      when(requestBuilder.execute[UpstreamErrorResponse](any[HttpReads[UpstreamErrorResponse]], any[ExecutionContext]))
        .thenReturn(Future.successful(UpstreamErrorResponse("400", Status.BAD_REQUEST)))

      val response: RequestOutcome[Boolean] = await(connector.archive(id))

      response shouldBe Left(BadRequestError)
    }
  }

  "Archive Connector" should {
    "Return a list of process summaries" in new Test {
      when(requestBuilder.execute[RequestOutcome[List[ProcessSummary]]](any[HttpReads[RequestOutcome[List[ProcessSummary]]]], any[ExecutionContext]))
        .thenReturn(Future.successful(Right(List(processSummary))))

      val response: RequestOutcome[List[ProcessSummary]] = await(connector.summaries)

      response shouldBe Right(List(processSummary))
    }

    "Return a published process json by id" in new Test {
      when(requestBuilder.execute[RequestOutcome[JsValue]](any[HttpReads[RequestOutcome[JsValue]]], any[ExecutionContext]))
        .thenReturn(Future.successful(Right(process)))

      val response: RequestOutcome[JsValue] = await(connector.getArchivedById(id))

      response shouldBe Right(process)
    }
  }
}

