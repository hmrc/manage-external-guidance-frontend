/*
 * Copyright 2021 HM Revenue & Customs
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
import models.TimescalesDetail
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json.JsValue
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpClient
import config.AppConfig
import models.RequestOutcome

@Singleton
class TimescalesConnector @Inject() (httpClient: HttpClient, appConfig: AppConfig) {
  import connectors.httpParsers.TimescalesHttpParser.timescalesHttpReads

  def details()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[TimescalesDetail]] = {
    val endPoint: String = appConfig.externalGuidanceBaseUrl + "/external-guidance/timescales"
    httpClient.GET[RequestOutcome[TimescalesDetail]](endPoint, Seq.empty, Seq.empty)
  }

  def submitTimescales(timescales: JsValue)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[TimescalesDetail]] = {
    val endpoint: String = appConfig.externalGuidanceBaseUrl + "/external-guidance/timescales"
    httpClient.POST[JsValue, RequestOutcome[TimescalesDetail]](endpoint, timescales, Seq.empty)
  }

}