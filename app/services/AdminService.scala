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

import javax.inject.{Inject, Singleton}
import config.AppConfig
import uk.gov.hmrc.http.HeaderCarrier
import scala.concurrent.{Future, ExecutionContext}
import connectors.AdminConnector
import models.{ManagedProcess, RequestOutcome}

@Singleton
class AdminService @Inject() (appConfig : AppConfig, adminConnector: AdminConnector) {

  def processesForApproval(implicit hc: HeaderCarrier, context: ExecutionContext): Future[RequestOutcome[List[ManagedProcess]]] =  
    adminConnector.processesForApproval
}
