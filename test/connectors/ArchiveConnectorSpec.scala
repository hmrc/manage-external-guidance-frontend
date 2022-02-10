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

import base.BaseSpec
import mocks.{MockAppConfig, MockHttpClient}
import models.errors.{BadRequestError, InvalidProcessError}
import models.{PublishedProcess, RequestOutcome}
import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, UpstreamErrorResponse}

import java.time.ZonedDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ArchiveConnectorSpec extends BaseSpec {

  private trait Test extends MockHttpClient with FutureAwaits with DefaultAwaitTimeout {

    implicit val hc: HeaderCarrier = HeaderCarrier()

    val connector: ArchiveConnector = new ArchiveConnector(mockHttpClient, MockAppConfig)

    val id: String = "Oct90005"

  }


  "Calling method archive with a dummy id" should {

    trait ArchiveTest extends Test {

      val endpoint: String = MockAppConfig.externalGuidanceBaseUrl + s"/external-guidance/archive/$id"

    }
    "Return true for a successful call" in new ArchiveTest {

      MockedHttpClient
        .get(endpoint)
        .returns(Future.successful(HttpResponse(Status.OK, "200")))

      val response: RequestOutcome[Boolean] =
        await(connector.archive(id))

      response shouldBe Right(true)
    }

    "Return an instance of an error class when an error occurs" in new ArchiveTest {

      MockedHttpClient
        .get(endpoint)
        .returns(Future.failed(UpstreamErrorResponse("400", Status.BAD_REQUEST)))

      val response: RequestOutcome[Boolean] =
        await(connector.archive(id))

      response shouldBe Left(BadRequestError)
    }
  }

  "Calling method getPublished with a dummy process" should {

    trait getPublishedTest extends Test {

      val endpoint: String = MockAppConfig.externalGuidanceBaseUrl + s"/external-guidance/published-process/$id"

    }
    "Return an instance of the class ApprovalResponse for a successful call" in new getPublishedTest {

      val expected: PublishedProcess = PublishedProcess(
        id, 1, ZonedDateTime.now(), Json.obj("a"->"b"), "", ""
      )

      MockedHttpClient
        .get(endpoint)
        .returns(Future.successful(Right(expected)))

      val response: RequestOutcome[PublishedProcess] =
        await(connector.getPublished(id))

      response shouldBe Right(expected)
    }

    "Return an instance of an error class when an error occurs" in new getPublishedTest {

      MockedHttpClient
        .get(endpoint)
        .returns(Future.successful(Left(InvalidProcessError)))

      val response: RequestOutcome[PublishedProcess] =
        await(connector.getPublished(id))

      response shouldBe Left(InvalidProcessError)
    }
  }

}
