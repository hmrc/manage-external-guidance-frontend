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

package services

import connectors.ApprovalConnector
import javax.inject.{Inject, Singleton}
import models.{ApprovalResponse, RequestOutcome, ApprovalProcessSummary}
import play.api.libs.json.JsValue
import uk.gov.hmrc.http.HeaderCarrier
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApprovalService @Inject() (connector: ApprovalConnector) {
  val logger = Logger(getClass)

  def approvalSummaries(implicit context: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[List[ApprovalProcessSummary]]] =
    connector.approvalSummaries

  def submitFor2iReview(process: JsValue)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[ApprovalResponse]] = {
    connector.submitFor2iReview(process)
  }

  def submitForFactCheck(process: JsValue)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[ApprovalResponse]] = {
    connector.submitForFactCheck(process)
  }
}
