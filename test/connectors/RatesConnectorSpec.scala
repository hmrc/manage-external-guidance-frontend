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
import models.RequestOutcome
import play.api.libs.json.{JsValue, Json}
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.http.HeaderCarrier
import java.time.ZonedDateTime
import models.{LabelledDataUpdateStatus, UpdateDetails}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RatesConnectorSpec extends BaseSpec {

  private trait Test extends MockHttpClientV2 with FutureAwaits with DefaultAwaitTimeout {
    val ratesJsonString =
      """
     |{
     |  "Legacy": {
     |      "higherrate":
     |      {
     |          "2016": 0.4,
     |          "2017": 45,
     |          "2018": 45,
     |          "2019": 45,
     |          "2020": 45,
     |          "2021": 45,
     |          "2022": 45
     |      },
     |      "basicrate":
     |      {
     |          "2016": 45,
     |          "2017": 45,
     |          "2018": 45,
     |          "2019": 45,
     |          "2020": 45,
     |          "2021": 45,
     |          "2022": 0.2
     |      }
     |  },
     |  "TaxNic": {
     |      "BasicRate":
     |      {
     |          "2016": 20,
     |          "2017": 20,
     |          "2018": 20,
     |          "2019": 20,
     |          "2020": 20,
     |          "2021": 20,
     |          "2022": 20
     |      },
     |      "CTC":
     |      {
     |          "2016": 0,
     |          "2017": 0,
     |          "2018": 0,
     |          "2019": 0,
     |          "2020": 0,
     |          "2021": 0,
     |          "2022": 0.567
     |      }
     |  }
     |}
    """.stripMargin

    val ratesTwoDimMap: Map[String, BigDecimal] = Map(
       ("Legacy!higherrate!2016" -> 0.4),
       ("Legacy!higherrate!2017" -> 45),
       ("Legacy!higherrate!2018" -> 45),
       ("Legacy!higherrate!2019" -> 45),
       ("Legacy!higherrate!2020" -> 45),
       ("Legacy!higherrate!2021" -> 45),
       ("Legacy!higherrate!2022" -> 45),
       ("Legacy!basicrate!2016" -> 45),
       ("Legacy!basicrate!2017" -> 45),
       ("Legacy!basicrate!2018" -> 45),
       ("Legacy!basicrate!2019" -> 45),
       ("Legacy!basicrate!2020" -> 45),
       ("Legacy!basicrate!2021" -> 45),
       ("Legacy!basicrate!2022" -> 0.2),
       ("TaxNic!BasicRate!2016" -> 20),
       ("TaxNic!BasicRate!2017" -> 20),
       ("TaxNic!BasicRate!2018" -> 20),
       ("TaxNic!BasicRate!2019" -> 20),
       ("TaxNic!BasicRate!2020" -> 20),
       ("TaxNic!BasicRate!2021" -> 20),
       ("TaxNic!BasicRate!2022" -> 20),
       ("TaxNic!CTC!2016" -> 0),
       ("TaxNic!CTC!2017" -> 0),
       ("TaxNic!CTC!2018" -> 0),
       ("TaxNic!CTC!2019" -> 0),
       ("TaxNic!CTC!2020" -> 0),
       ("TaxNic!CTC!2021" -> 0),
       ("TaxNic!CTC!2022" -> 0.567)
    )

    val dummyRates: JsValue = Json.parse("""{"TimescaleID": 10}""")
    val lastUpdateTime: ZonedDateTime = ZonedDateTime.of(2020, 1, 1, 12, 0, 1, 0, ZonedDateTime.now.getZone)
    val ratesJson: JsValue = Json.parse(ratesJsonString)
    val rates: Map[String, BigDecimal] = ratesTwoDimMap
    val credId: String = "234324234"
    val user: String = "User Blah"
    val email: String = "user@blah.com"
    val updateDetail = UpdateDetails(lastUpdateTime, "234324234", "User Blah", "user@blah.com")
    val ratesDetail = LabelledDataUpdateStatus(rates.size, Some(updateDetail))

    implicit val hc: HeaderCarrier = HeaderCarrier()

    val connector: RatesConnector = new RatesConnector(mockHttpClientV2, MockAppConfig)
  }


  "Calling method submitRates" should {
    val endpoint: String = MockAppConfig.externalGuidanceBaseUrl + "/external-guidance/rates"

    "Return an instance of the class LabelledDataUpdateStatus for a successful call" in new Test {

      MockedHttpClientV2
        .post(endpoint, ratesJson)
        .returns(Future.successful(Right(ratesDetail)))

      val response: RequestOutcome[LabelledDataUpdateStatus] =
        await(connector.submitRates(ratesJson))

      response shouldBe Right(ratesDetail)
    }

  }

  "Calling method details" should {
    val endpoint: String = MockAppConfig.externalGuidanceBaseUrl + "/external-guidance/rates"

    "Return an instance of the class LabelledDataUpdateStatus for a successful call" in new Test {

      MockedHttpClientV2
        .get(endpoint)
        .returns(Future.successful(Right(ratesDetail)))

      val response: RequestOutcome[LabelledDataUpdateStatus] = await(connector.details())

      response shouldBe Right(ratesDetail)
    }
  }
}
