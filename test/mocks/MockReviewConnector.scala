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

package mocks

import connectors.ReviewConnector
import models.{ApprovalProcessReview, ApprovalProcessStatusChange, RequestOutcome}
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockReviewConnector extends MockFactory {

  val mockReviewConnector = mock[ReviewConnector]

  object MockReviewConnector {

    def approval2iReview(id: String): CallHandler[Future[RequestOutcome[ApprovalProcessReview]]] = {

      (mockReviewConnector
        .approval2iReview(_: String)(_: ExecutionContext, _: HeaderCarrier))
        .expects(id, *, *)
    }

    def approval2iReviewComplete(id: String, info: ApprovalProcessStatusChange): CallHandler[Future[RequestOutcome[Unit]]] = {
      (mockReviewConnector
        .approval2iReviewComplete(_: String, _: ApprovalProcessStatusChange)(_: ExecutionContext, _: HeaderCarrier))
        .expects(id, info, *, *)
    }

  }
}
