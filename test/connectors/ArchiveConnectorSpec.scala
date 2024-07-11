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

import java.time.ZonedDateTime
import base.BaseSpec
import mocks.{MockAppConfig, MockHttpClientV2}
import models.errors.BadRequestError
import models.{ProcessSummary, RequestOutcome}
import play.api.http.Status
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, UpstreamErrorResponse}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.libs.json._

class ArchiveConnectorSpec extends BaseSpec {

  private trait Test extends MockHttpClientV2 with FutureAwaits with DefaultAwaitTimeout {

    implicit val hc: HeaderCarrier = HeaderCarrier()

    val connector: ArchiveConnector = new ArchiveConnector(mockHttpClientV2, MockAppConfig)

    val id: String = "Oct90005"
    val now = ZonedDateTime.now
    val processCode: String = "code"
    val process = Json.obj()
    val processSummary = ProcessSummary(id, processCode, 1, "author", None, now, "actionedby", "Status")
  }


  "Calling method archive with a dummy id" should {

    trait ArchiveTest extends Test {

      val endpoint: String = MockAppConfig.externalGuidanceBaseUrl + s"/external-guidance/archive/$id"

    }
    "Return true for a successful call" in new ArchiveTest {

      MockedHttpClientV2
        .get(endpoint)
        .returns(Future.successful(HttpResponse(Status.OK, "200")))

      val response: RequestOutcome[Boolean] =
        await(connector.archive(id))

      response shouldBe Right(true)
    }

    "Return an instance of an error class when an error occurs" in new ArchiveTest {

      MockedHttpClientV2
        .get(endpoint)
        .returns(Future.failed(UpstreamErrorResponse("400", Status.BAD_REQUEST)))

      val response: RequestOutcome[Boolean] =
        await(connector.archive(id))

      response shouldBe Left(BadRequestError)
    }
  }

  "Archive Connector" should {
    "Return a list of process summaries" in new Test {

      MockedHttpClientV2
        .get(MockAppConfig.externalGuidanceBaseUrl + s"/external-guidance/archived")
        .returns(Future.successful(Right(List(processSummary))))

      val response: RequestOutcome[List[ProcessSummary]] =
        await(connector.summaries)

      response shouldBe Right(List(processSummary))
    }

    "Return a published process json by id" in new Test {

      MockedHttpClientV2
        .get(MockAppConfig.externalGuidanceBaseUrl + s"/external-guidance/archived/$id")
        .returns(Future.successful(Right(process)))

      val response: RequestOutcome[JsValue] =
        await(connector.getArchivedById(id))

      response shouldBe Right(process)
    }
  }


}

