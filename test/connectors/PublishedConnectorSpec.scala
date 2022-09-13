/*
 * Copyright 2022 HM Revenue & Customs
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
import mocks.{MockAppConfig, MockHttpClient}
import models.{ProcessSummary, RequestOutcome}
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.http.HeaderCarrier
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.libs.json._
import models.PublishedProcess

class PublishedConnectorSpec extends BaseSpec {

  private trait Test extends MockHttpClient with FutureAwaits with DefaultAwaitTimeout {

    implicit val hc: HeaderCarrier = HeaderCarrier()

    val connector: PublishedConnector = new PublishedConnector(mockHttpClient, MockAppConfig)

    val id: String = "Oct90005"
    val now = ZonedDateTime.now
    val processCode: String = "code"
    val process = Json.obj()
    val processSummary = ProcessSummary(id, processCode, 1, "author", None, now, "actionedby", "Status")
    val publishedProcess = PublishedProcess(id, 1, now, process, "author", processCode)
  }


  "PublishedConnector" should {

    "Return a list of process summaries" in new Test {

      MockedHttpClient
        .get(MockAppConfig.externalGuidanceBaseUrl + s"/external-guidance/published")
        .returns(Future.successful(Right(List(processSummary))))

      val response: RequestOutcome[List[ProcessSummary]] =
        await(connector.summaries)

      response shouldBe Right(List(processSummary))
    }

    "Return a Published Process by id" in new Test {

      MockedHttpClient
        .get(MockAppConfig.externalGuidanceBaseUrl + s"/external-guidance/published-process/$id")
        .returns(Future.successful(Right(publishedProcess)))

      val response: RequestOutcome[PublishedProcess] =
        await(connector.getPublished(id))

      response shouldBe Right(publishedProcess)
    }

    "Return a published process json by process code" in new Test {

      MockedHttpClient
        .get(MockAppConfig.externalGuidanceBaseUrl + s"/external-guidance/published/$processCode")
        .returns(Future.successful(Right(process)))

      val response: RequestOutcome[JsValue] =
        await(connector.getPublishedByProcessCode(processCode))

      response shouldBe Right(process)
    }
  }
}
