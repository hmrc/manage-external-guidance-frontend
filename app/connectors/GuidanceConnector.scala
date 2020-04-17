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

import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json.JsValue
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import config.AppConfig
import models.{RequestOutcome, SaveScratchSubmissionResponse, SaveSubmittedProcessResponse}
import connectors.httpParsers.SaveSubmittedProcessHttpParser.saveSubmittedProcessHttpReads

@Singleton
class GuidanceConnector @Inject() (httpClient: HttpClient, appConfig: AppConfig) {

  def submitScratchProcess(process: JsValue)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[SaveScratchSubmissionResponse]] = {

    import connectors.httpParsers.SaveScratchProcessHttpParser.saveScratchProcessHttpReads

    val endpoint: String = appConfig.externalGuidanceBaseUrl + "/external-guidance/scratch"

    httpClient.POST[JsValue, RequestOutcome[SaveScratchSubmissionResponse]](endpoint, process, Seq.empty)
  }

  def makeAvailableForApproval(process: JsValue)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[SaveSubmittedProcessResponse]] = {

    val endpoint: String = appConfig.externalGuidanceBaseUrl + "/external-guidance/submitted"

    httpClient.POST[JsValue, RequestOutcome[SaveSubmittedProcessResponse]](endpoint, process, Seq.empty)
  }

}
