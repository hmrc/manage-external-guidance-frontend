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
import mocks.{MockAppConfig, MockHttpClient}
import models.RequestOutcome
import models.admin.CachedProcessSummary
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.http.HeaderCarrier
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.libs.json._


class ViewerConnectorSpec extends BaseSpec {

  trait Test extends MockHttpClient with FutureAwaits with DefaultAwaitTimeout {

    implicit val hc: HeaderCarrier = HeaderCarrier()

    val connector: ViewerConnector = new ViewerConnector(mockHttpClient, MockAppConfig)
    val cp = CachedProcessSummary("id", 123456789L, Some(123456789L), Some(123456789L), "A title", Instant.now)
    val someJson: JsValue = Json.toJson(cp)
    val oneItemList: List[CachedProcessSummary] = List(cp)
  }

  "PublishedConnector" should {

    "Return a list of process summaries" in new Test {

      MockedHttpClient
        .get(MockAppConfig.activeProcessesUrl)
        .returns(Future.successful(Right(oneItemList)))

      val response: RequestOutcome[List[CachedProcessSummary]] =
        await(connector.listActive())

      response shouldBe Right(oneItemList)
    }

    "Return a Published Process by id" in new Test {
      val queryParams: Seq[(String, String)] = Seq(("timescalesVersion", "123456789"), ("ratesVersion", "123456789"))
      MockedHttpClient
        .get(MockAppConfig.activeProcessesUrl + s"/${cp.id}/${cp.processVersion}", queryParams)
        .returns(Future.successful(Right(someJson)))

      val response: RequestOutcome[JsValue] =
        await(connector.get(cp.id, cp.processVersion, Some(123456789L), Some(123456789L)))

      response shouldBe Right(someJson)
    }

  }
}
