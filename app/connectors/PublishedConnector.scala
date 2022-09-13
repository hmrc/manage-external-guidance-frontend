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

import config.AppConfig
import javax.inject.{Inject, Singleton}
import models.{ProcessSummary, PublishedProcess,RequestOutcome}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpClient
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json.JsValue

@Singleton
class PublishedConnector @Inject() (httpClient: HttpClient, appConfig: AppConfig) {

  def summaries(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[List[ProcessSummary]]] = {
    import connectors.httpParsers.PublishedProcessHttpParser.processSummaryHttpReads

    val summaryEndPoint: String = appConfig.externalGuidanceBaseUrl + "/external-guidance/published"
    httpClient.GET[RequestOutcome[List[ProcessSummary]]](summaryEndPoint, Seq.empty, Seq.empty)
  }

  def getPublished(id: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[PublishedProcess]] = {
    val publishedEndPoint: String = appConfig.externalGuidanceBaseUrl + s"/external-guidance/published-process/$id"

    import connectors.httpParsers.PublishedProcessHttpParser.publishedProcessHttpReads
    httpClient.GET[RequestOutcome[PublishedProcess]](publishedEndPoint)
  }

  def getPublishedByProcessCode(code: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[JsValue]] = {
    val publishedEndPoint: String = appConfig.externalGuidanceBaseUrl + s"/external-guidance/published/$code"

    import connectors.httpParsers.PublishedProcessHttpParser.processHttpReads
    httpClient.GET[RequestOutcome[JsValue]](publishedEndPoint)
  }

}
