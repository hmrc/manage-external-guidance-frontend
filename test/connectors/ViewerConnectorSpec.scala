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

import java.time.Instant
import base.BaseSpec
import mocks.{MockAppConfig}
import models.RequestOutcome
import models.admin.CachedProcessSummary
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._
import uk.gov.hmrc.http.HttpReads
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when

class ViewerConnectorSpec extends BaseSpec {

  trait Test extends ConnectorTest {
    val connector: ViewerConnector = new ViewerConnector(mockHttpClient, MockAppConfig)
    val cp = CachedProcessSummary("id", 123456789L, Some(123456789L), Some(123456789L), "A title", Instant.now)
    val someJson: JsValue = Json.toJson(cp)
    val oneItemList: List[CachedProcessSummary] = List(cp)
  }

  "PublishedConnector" should {

    "Return a list of process summaries" in new Test {

      when(requestBuilder.execute[RequestOutcome[List[CachedProcessSummary]]](any[HttpReads[RequestOutcome[List[CachedProcessSummary]]]], any[ExecutionContext]))
        .thenReturn(Future.successful(Right(oneItemList)))

      val response: RequestOutcome[List[CachedProcessSummary]] =
        await(connector.listActive())

      response shouldBe Right(oneItemList)
    }

    "Return a Published Process by id" in new Test {

      when(requestBuilder.execute[RequestOutcome[JsValue]](any[HttpReads[RequestOutcome[JsValue]]], any[ExecutionContext]))
        .thenReturn(Future.successful(Right(someJson)))

      val response: RequestOutcome[JsValue] =
        await(connector.get(cp.id, cp.processVersion, Some(123456789L), Some(123456789L)))

      response shouldBe Right(someJson)
    }

  }
}
