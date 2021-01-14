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

import config.AppConfig
import javax.inject.{Inject, Singleton}
import models.{ApprovalProcessSummary, ApprovalResponse, RequestOutcome}
import play.api.libs.json.JsValue
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApprovalConnector @Inject() (httpClient: HttpClient, appConfig: AppConfig) {

  def approvalSummaries(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[List[ApprovalProcessSummary]]] = {

    import connectors.httpParsers.ApprovalHttpParser.getApprovalSummaryListHttpReads

    val summaryEndPoint: String = appConfig.externalGuidanceBaseUrl + "/external-guidance/approval"

    httpClient.GET[RequestOutcome[List[ApprovalProcessSummary]]](summaryEndPoint, Seq.empty, Seq.empty)
  }

  def submitFor2iReview(process: JsValue)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[ApprovalResponse]] = {

    import connectors.httpParsers.ApprovalHttpParser.saveApprovalHttpReads

    val endpoint: String = appConfig.externalGuidanceBaseUrl + "/external-guidance/approval/2i-review"

    httpClient.POST[JsValue, RequestOutcome[ApprovalResponse]](endpoint, process, Seq.empty)
  }

  def submitForFactCheck(process: JsValue)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[ApprovalResponse]] = {

    import connectors.httpParsers.ApprovalHttpParser.saveApprovalHttpReads

    val endpoint: String = appConfig.externalGuidanceBaseUrl + "/external-guidance/approval/fact-check"

    httpClient.POST[JsValue, RequestOutcome[ApprovalResponse]](endpoint, process, Seq.empty)
  }
}
