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

import connectors.ApprovalConnector
import models.{ApprovalResponse, RequestOutcome}
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import play.api.libs.json.JsValue
import uk.gov.hmrc.http.HeaderCarrier
import models.ManagedProcess
import scala.concurrent.{ExecutionContext, Future}

trait MockApprovalConnector extends MockFactory {

  val mockApprovalConnector: ApprovalConnector = mock[ApprovalConnector]

  object MockApprovalConnector {

    def processesForApproval(implicit ec: ExecutionContext, hc: HeaderCarrier): CallHandler[Future[RequestOutcome[List[ManagedProcess]]]] =
      (mockApprovalConnector
        .processesForApproval(_: ExecutionContext, _: HeaderCarrier))
        .expects(*, *)

    def submitForApproval(process: JsValue): CallHandler[Future[RequestOutcome[ApprovalResponse]]] = {

      (mockApprovalConnector
        .submitForApproval(_: JsValue)(_: ExecutionContext, _: HeaderCarrier))
        .expects(process, *, *)
    }

  }

}
