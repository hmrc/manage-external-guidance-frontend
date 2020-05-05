/*
 * Copyright 2020 HM Revenue & Customs
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
import models.{RequestOutcome, ApprovalResponse, ApprovalProcessSummary}
import models.ApprovalStatus._
import play.api.libs.json.JsValue
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApprovalConnector @Inject() (httpClient: HttpClient, appConfig: AppConfig) {

  //noinspection ScalaStyle
  val stubProcessList = List(
    ApprovalProcessSummary("ext90002", "Telling hmrc about extra income", LocalDate.of(2020, 2, 20), SubmittedFor2iReview),
    ApprovalProcessSummary("ext90003", "EU exit guidance", LocalDate.of(2020, 2, 7), SubmittedForFactCheck),
    ApprovalProcessSummary("ext90004", "Find a lost user ID and password", LocalDate.of(2019, 12, 13), SubmittedFor2iReview),
    ApprovalProcessSummary("ext90005", "Apply for a marriage licence", LocalDate.of(2019, 12, 12), SubmittedFor2iReview)
  )

  def approvalSummaries(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[List[ApprovalProcessSummary]]] =
    Future.successful(Right(stubProcessList))

  def submitForApproval(process: JsValue)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[ApprovalResponse]] = {

    import connectors.httpParsers.ApprovalHttpParser.saveApprovalHttpReads

    val endpoint: String = appConfig.externalGuidanceBaseUrl + "/external-guidance/approval"

    httpClient.POST[JsValue, RequestOutcome[ApprovalResponse]](endpoint, process, Seq.empty)
  }

}
