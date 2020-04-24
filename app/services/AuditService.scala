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
import play.api.libs.json.JodaWrites
import play.api.libs.json._
import org.joda.time.DateTime
import play.api.Logger
import play.api.http.HeaderNames
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.AuditExtensions
import uk.gov.hmrc.play.audit.http.connector.AuditResult.{Disabled, Failure, Success}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.audit.model.ExtendedDataEvent
import scala.concurrent.ExecutionContext
import models.audit.AuditEvent

@Singleton
class AuditService @Inject() (appConfig: AppConfig, auditConnector: AuditConnector) {
  private val logger = Logger(getClass)
  private val referrer: HeaderCarrier => String = _.headers.find(_._1 == HeaderNames.REFERER).map(_._2).getOrElse("-")

  implicit val dateTimeWriter: Writes[DateTime] = JodaWrites.jodaDateWrites("dd/MM/yyyy HH:mm:ss")
  implicit val extendedDataEventWrites: Writes[ExtendedDataEvent] = Json.writes[ExtendedDataEvent]

  private def toExtendedDataEvent(event: AuditEvent, path: Option[String])(implicit hc: HeaderCarrier): ExtendedDataEvent =
    ExtendedDataEvent(
      auditSource = appConfig.appName,
      auditType = event.auditType,
      tags = AuditExtensions.auditHeaderCarrier(hc).toAuditTags(event.transactionName, path.fold(referrer(hc))(x => x)),
      detail = Json.toJson(AuditExtensions.auditHeaderCarrier(hc).toAuditDetails()).as[JsObject].deepMerge(event.detail.as[JsObject])
    )

  def audit(event: AuditEvent, path: Option[String] = None)(implicit hc: HeaderCarrier, context: ExecutionContext): Unit =
    auditConnector.sendExtendedEvent(toExtendedDataEvent(event, path)).map {
      case Success => logger.info(s"Audit successful")
      case Failure(err, _) => logger.warn(s"Audit failed with error $err")
      case Disabled => logger.info("Auditing Disabled")
    }
}
