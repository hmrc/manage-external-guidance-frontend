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
import connectors.httpParsers.ReviewHttpParser._
import javax.inject.{Inject, Singleton}
import models.audit.AuditInfo
import models.{ApprovalProcessReview, ApprovalProcessStatusChange, PageReviewDetail, RequestOutcome}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ReviewConnector @Inject() (httpClient: HttpClient, appConfig: AppConfig) {

  def approval2iReview(id: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[ApprovalProcessReview]] = {

    val reviewEndPoint: String = s"${appConfig.externalGuidanceBaseUrl}/external-guidance/approval/$id/2i-review"

    httpClient.GET[RequestOutcome[ApprovalProcessReview]](reviewEndPoint)
  }

  def approval2iReviewConfirmCheck(id: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[Unit]] = {

    val reviewEndPoint: String = s"${appConfig.externalGuidanceBaseUrl}/external-guidance/approval/$id/2i-review/confirm"

    httpClient.GET[RequestOutcome[Unit]](reviewEndPoint)
  }

  def approval2iReviewComplete(
      id: String,
      info: ApprovalProcessStatusChange
  )(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[AuditInfo]] = {

    val reviewEndPoint: String = s"${appConfig.externalGuidanceBaseUrl}/external-guidance/approval/$id/2i-review"

    httpClient.POST[ApprovalProcessStatusChange, RequestOutcome[AuditInfo]](reviewEndPoint, info)
  }

  def approval2iReviewPageInfo(id: String, pageUrl: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[PageReviewDetail]] = {

    val reviewEndPoint: String = s"${appConfig.externalGuidanceBaseUrl}/external-guidance/approval/$id/2i-page-review$pageUrl"

    httpClient.GET[RequestOutcome[PageReviewDetail]](reviewEndPoint)
  }

  def approval2iReviewPageComplete(
      id: String,
      pageUrl: String,
      info: PageReviewDetail
  )(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[Unit]] = {

    val reviewEndPoint: String = s"${appConfig.externalGuidanceBaseUrl}/external-guidance/approval/$id/2i-page-review$pageUrl"
    httpClient.POST[PageReviewDetail, RequestOutcome[Unit]](reviewEndPoint, info)
  }

  def approvalFactCheck(id: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[ApprovalProcessReview]] = {

    val reviewEndPoint: String = s"${appConfig.externalGuidanceBaseUrl}/external-guidance/approval/$id/fact-check"

    httpClient.GET[RequestOutcome[ApprovalProcessReview]](reviewEndPoint)
  }

  def approvalFactCheckComplete(
      id: String,
      info: ApprovalProcessStatusChange
  )(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[AuditInfo]] = {

    val reviewEndPoint: String = s"${appConfig.externalGuidanceBaseUrl}/external-guidance/approval/$id/fact-check"

    httpClient.POST[ApprovalProcessStatusChange, RequestOutcome[AuditInfo]](reviewEndPoint, info)
  }

  def factCheckPageInfo(id: String, pageUrl: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[PageReviewDetail]] = {

    val reviewEndPoint: String = s"${appConfig.externalGuidanceBaseUrl}/external-guidance/approval/$id/fact-check-page-review$pageUrl"

    httpClient.GET[RequestOutcome[PageReviewDetail]](reviewEndPoint)
  }

  def factCheckPageComplete(id: String, pageUrl: String, info: PageReviewDetail)(
      implicit ec: ExecutionContext,
      hc: HeaderCarrier
  ): Future[RequestOutcome[Unit]] = {

    val reviewEndPoint: String = s"${appConfig.externalGuidanceBaseUrl}/external-guidance/approval/$id/fact-check-page-review$pageUrl"

    httpClient.POST[PageReviewDetail, RequestOutcome[Unit]](reviewEndPoint, info)
  }

}
