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

import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import play.api.libs.json.Writes
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads}

import scala.concurrent.{ExecutionContext, Future}

trait MockHttpClient extends MockFactory {

  val mockHttpClient: HttpClient = mock[HttpClient]

  object MockedHttpClient {

    def post[I, O](url: String, body: I): CallHandler[Future[O]] = {
      (mockHttpClient
        .POST[I, O](_: String, _: I, _: Seq[(String, String)])(_: Writes[I], _: HttpReads[O], _: HeaderCarrier, _: ExecutionContext))
        .expects(url, body, *, *, *, *, *)
    }

    def get[O](url: String): CallHandler[Future[O]] = {
      (mockHttpClient
        .GET[O](_: String, _: Seq[(String, String)], _: Seq[(String, String)])(_: HttpReads[O], _: HeaderCarrier, _: ExecutionContext))
        .expects(url, *, *, *, *, *)
    }

  }

}
