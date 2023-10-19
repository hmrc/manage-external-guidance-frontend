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
import models.admin.CachedProcessSummary
import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpClient
import config.AppConfig
import models.RequestOutcome
import play.api.libs.json.JsValue

@Singleton
class ViewerConnector @Inject() (httpClient: HttpClient, appConfig: AppConfig) {

  def listActive()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[List[CachedProcessSummary]]] = {
    val endPoint: String = appConfig.activeProcessesUrl
    import connectors.httpParsers.ActiveProcessHttpParser.summaryHttpReads
    httpClient.GET[RequestOutcome[List[CachedProcessSummary]]](endPoint, Seq.empty, Seq.empty)
  }

  def get(id: String, version: Long)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[JsValue]] = {
    val endPoint: String = appConfig.activeProcessesUrl + s"/$id/${version.toString}"
    import connectors.httpParsers.ActiveProcessHttpParser.processHttpReads
    httpClient.GET[RequestOutcome[JsValue]](endPoint, Seq.empty, Seq.empty)
  }

}
