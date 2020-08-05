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

package services

import connectors.ScratchConnector
import javax.inject.{Inject, Singleton}
import models.{RequestOutcome, ScratchResponse}
import play.api.libs.json.JsValue
import uk.gov.hmrc.http.HeaderCarrier
import models.ocelot.Process
import models.errors._
import play.api.Logger
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ScratchService @Inject() (scratchConnector: ScratchConnector, pageBuilder: PageBuilder) {
  val logger = Logger(getClass)

  def submitScratchProcess(json: JsValue)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[RequestOutcome[ScratchResponse]] = 
    json.validate[Process].fold(err => {
      logger.error(s"Scratch process validation has failed with error $err")
      Future.successful(Left(BadRequestError))
      }, process => 
      pageBuilder.pages(process).fold(err => Future.successful(Left(toError(List(err)))), _ => scratchConnector.submitScratchProcess(json))
    )

}
