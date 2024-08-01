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

package mocks

import connectors.ViewerConnector
import models.RequestOutcome
import models.admin.CachedProcessSummary
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import org.scalatest.TestSuite
import uk.gov.hmrc.http.HeaderCarrier
import play.api.libs.json.JsValue

import scala.concurrent.{ExecutionContext, Future}

trait MockViewerConnector extends TestSuite with MockFactory {

  val mockViewerConnector: ViewerConnector = mock[ViewerConnector]

  object MockViewerConnector {

    def listActive: CallHandler[Future[RequestOutcome[List[CachedProcessSummary]]]] =
      (mockViewerConnector
        .listActive()(_: ExecutionContext, _: HeaderCarrier))
        .expects(*, *)

    def get(id: String, version: Long, timescalesVersion: Option[Long], ratesVersion: Option[Long]): CallHandler[Future[RequestOutcome[JsValue]]] = {
      (mockViewerConnector
        .get(_: String, _: Long, _: Option[Long], _: Option[Long])(_: ExecutionContext, _: HeaderCarrier))
        .expects(id, version, timescalesVersion, ratesVersion, *, *)
    }

  }

}
