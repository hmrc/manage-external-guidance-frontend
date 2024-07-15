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
import models.RequestOutcome
import play.api.libs.json.{JsValue, Json}
import java.time.ZonedDateTime
import models.{LabelledDataUpdateStatus, UpdateDetails}
import scala.concurrent.ExecutionContext.Implicits.global
import org.mockito.Mockito.when
import scala.concurrent.Future

class TimescalesConnectorSpec extends BaseSpec {

  private trait Test extends ConnectorTest {
    val dummyTimescales: JsValue = Json.parse("""{"TimescaleID": 10}""")
    val lastUpdateTime: ZonedDateTime = ZonedDateTime.of(2020, 1, 1, 12, 0, 1, 0, ZonedDateTime.now.getZone)
    val timescalesJson: JsValue = Json.parse("""{"First": 1, "Second": 2, "Third": 3}""")
    val timescales: Map[String, Int] = Map("First" -> 1, "Second" -> 2, "Third" -> 3)
    val credId: String = "234324234"
    val user: String = "User Blah"
    val email: String = "user@blah.com"
    val updateDetail = UpdateDetails(lastUpdateTime, "234324234", "User Blah", "user@blah.com")
    val timescalesDetail = LabelledDataUpdateStatus(timescales.size, Some(updateDetail))

    val connector: TimescalesConnector = new TimescalesConnector(mockHttpClient, MockAppConfig)
  }


  "Calling method submitTimescales" should {

    "Return an instance of the class ApprovalResponse for a successful call" in new Test {

      // MockedHttpClientV2
      //   .post(url"$endpoint")
      //   .returns(FakeRequestBuilder(Future.successful(Right(timescalesDetail))))
      when(requestBuilderExecute[RequestOutcome[LabelledDataUpdateStatus]]).thenReturn(Future.successful(Right(timescalesDetail)))

      val response: RequestOutcome[LabelledDataUpdateStatus] = await(connector.submitTimescales(timescalesJson))

      response shouldBe Right(timescalesDetail)
    }

  }

  "Calling method details" should {

    "Return an instance of the class ApprovalResponse for a successful call" in new Test {

      // MockedHttpClientV2
      //   .get(url"$endpoint")
      //   .returns(FakeRequestBuilder(Future.successful(Right(timescalesDetail))))
      when(requestBuilderExecute[RequestOutcome[LabelledDataUpdateStatus]]).thenReturn(Future.successful(Right(timescalesDetail)))

      val response: RequestOutcome[LabelledDataUpdateStatus] = await(connector.details())

      response shouldBe Right(timescalesDetail)
    }
  }
}
