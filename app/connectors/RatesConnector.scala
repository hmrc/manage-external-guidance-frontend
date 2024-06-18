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

import javax.inject.{Inject, Singleton}
import models.LabelledDataUpdateStatus
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json.JsValue
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpClient
import config.AppConfig
import models.RequestOutcome

@Singleton
class RatesConnector @Inject() (httpClient: HttpClient, appConfig: AppConfig) {
  import connectors.httpParsers.LabelledDataHttpParser.labelledDataHttpReads

  def details()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[LabelledDataUpdateStatus]] = {
    val endPoint: String = appConfig.externalGuidanceBaseUrl + "/external-guidance/rates"
    httpClient.GET[RequestOutcome[LabelledDataUpdateStatus]](endPoint, Seq.empty, Seq.empty)
  }

  def submitRates(rates: JsValue)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[LabelledDataUpdateStatus]] = {
    val endpoint: String = appConfig.externalGuidanceBaseUrl + "/external-guidance/rates"
    httpClient.POST[JsValue, RequestOutcome[LabelledDataUpdateStatus]](endpoint, rates, Seq.empty)
  }

  def get()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[JsValue]] = {
    val endPoint: String = appConfig.externalGuidanceBaseUrl + "/external-guidance/rates/data"
    import connectors.httpParsers.PublishedProcessHttpParser.processHttpReads
    httpClient.GET[RequestOutcome[JsValue]](endPoint, Seq.empty, Seq.empty)
  }

}
