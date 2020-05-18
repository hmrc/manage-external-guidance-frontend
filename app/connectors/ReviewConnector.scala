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

import config.AppConfig
import connectors.httpParsers.ReviewHttpParser.getReviewDetailsHttpReads
import javax.inject.{Inject, Singleton}
import models.{ApprovalProcessReview, RequestOutcome}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ReviewConnector @Inject()(httpClient: HttpClient, appConfig: AppConfig) {

  def approval2iReview(id: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[ApprovalProcessReview]] = {

    val reviewEndPoint: String = s"${appConfig.externalGuidanceBaseUrl}/external-guidance/approval/$id/2i-review"

    httpClient.GET[RequestOutcome[ApprovalProcessReview]](reviewEndPoint)
  }
}
