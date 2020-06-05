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

package services

import connectors.ReviewConnector
import javax.inject.{Inject, Singleton}
import models.{ApprovalProcessReview, ApprovalProcessStatusChange, ApprovalStatus, RequestOutcome}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ReviewService @Inject() (reviewConnector: ReviewConnector) {

  def approval2iReview(id: String)(implicit ex: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[ApprovalProcessReview]] = {

    reviewConnector.approval2iReview(id)

  }

  def approval2iReviewComplete(id: String, status: ApprovalStatus)(implicit ex: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[Unit]] = {

    val changeInfo = ApprovalProcessStatusChange("userId", "userName", status)
    reviewConnector.approval2iReviewComplete(id, changeInfo)

  }

  def approvalFactCheck(id: String)(implicit ex: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[ApprovalProcessReview]] = {

    reviewConnector.approvalFactCheck(id)
  }

  def approvalFactCheckComplete(id: String, status: ApprovalStatus)(implicit ex: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[Unit]] = {

    val changeInfo = ApprovalProcessStatusChange("userId", "userName", status)
    reviewConnector.approvalFactCheckComplete(id, changeInfo)
  }
}
