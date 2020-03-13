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

import java.util.UUID.randomUUID

import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}
import play.api.http.{ContentTypes, HeaderNames}
import play.api.libs.json.JsValue
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import config.AppConfig
import models.{RequestOutcome, ScratchProcessSubmissionResponse}

@Singleton
class GuidanceConnector @Inject() (httpClient: HttpClient, appConfig: AppConfig) {

  private[connectors] val stubbedResponse: RequestOutcome[ScratchProcessSubmissionResponse] = Right(ScratchProcessSubmissionResponse(randomUUID.toString))

  private val headers = Seq(HeaderNames.CONTENT_TYPE -> ContentTypes.JSON)

  def submitScratchProcess(process: JsValue)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[ScratchProcessSubmissionResponse]] = {

    import connectors.httpParsers.GetScratchProcessSubmissionHttpParser.getScratchProcessSubmissionHttpReads

    val endpoint: String = appConfig.externalGuidanceScratchUrl

    httpClient.POSTString[RequestOutcome[ScratchProcessSubmissionResponse]](endpoint, process.toString(), headers)(
      getScratchProcessSubmissionHttpReads,
      implicitly,
      implicitly
    )
  }

}