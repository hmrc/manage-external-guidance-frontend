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

package connectors

import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json.{Json, JsValue}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import config.AppConfig
import models.{RequestOutcome, ScratchResponse}
import uk.gov.hmrc.http.StringContextOps

@Singleton
class ScratchConnector @Inject() (httpClient: HttpClientV2, appConfig: AppConfig) {

  def submitScratchProcess(process: JsValue)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[ScratchResponse]] = {

    import connectors.httpParsers.ScratchHttpParser.saveScratchProcessHttpReads

    val endpoint: String = appConfig.externalGuidanceBaseUrl + "/external-guidance/scratch"

    httpClient.post(url"$endpoint").withBody(Json.toJson(process)).execute[RequestOutcome[ScratchResponse]]
  }

}
