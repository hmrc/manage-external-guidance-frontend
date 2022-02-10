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

package mocks

import connectors.ArchiveConnector
import models.{PublishedProcess, RequestOutcome}
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockArchiveConnector extends MockFactory {

  val mockArchiveConnector: ArchiveConnector = mock[ArchiveConnector]

  object MockArchiveConnector {

    def archive(id: String): CallHandler[Future[RequestOutcome[Boolean]]] =
      (mockArchiveConnector
        .archive(_: String)(_: ExecutionContext, _: HeaderCarrier))
        .expects(id, *, *)

    def getPublished(id: String): CallHandler[Future[RequestOutcome[PublishedProcess]]] = {
      (mockArchiveConnector
        .getPublished(_: String)(_: ExecutionContext, _: HeaderCarrier))
        .expects(id, *, *)
    }

  }

}
