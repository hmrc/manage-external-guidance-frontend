/*
 * Copyright 2021 HM Revenue & Customs
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
import models.errors.BadRequestError
import models.{PublishedProcess, RequestOutcome}
import play.api.Logging
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ArchiveConnector @Inject()(httpClient: HttpClient, appConfig: AppConfig) extends Logging {

  def archive(id: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[Boolean]] = {

    val archiveEndPoint: String = appConfig.externalGuidanceBaseUrl + s"/external-guidance/archive/$id"

    import uk.gov.hmrc.http.HttpReads.Implicits.readRaw

    httpClient.GET[HttpResponse](archiveEndPoint)
      .map { _ => Right(true) }
      .recover {
        case ex =>
          logger.error(s"Error archiving provess $id with exception: ${ex.getMessage}")
          Left(BadRequestError)
      }
  }

  def getPublished(id: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[PublishedProcess]] = {

    val publishedEndPoint: String = appConfig.externalGuidanceBaseUrl + s"/external-guidance/published-process/$id"

    import connectors.httpParsers.PublishedProcessHttpParser.publishedProcessHttpReads

    httpClient.GET[RequestOutcome[PublishedProcess]](publishedEndPoint)
  }

}
