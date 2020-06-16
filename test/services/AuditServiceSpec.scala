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

import scala.concurrent.ExecutionContext.Implicits.global
import java.util.UUID
import uk.gov.hmrc.http.HeaderCarrier
import org.joda.time.DateTime
import base.BaseSpec
import play.api.libs.json._
import mocks.{MockAppConfig, MockAuditConnector}
import uk.gov.hmrc.play.audit.model.ExtendedDataEvent
import models.audit.TwoEyeReviewCompleteEvent
import models.audit._

class AuditServiceSpec extends BaseSpec {

  private trait Test extends MockAuditConnector {
    implicit val headerCarrier: HeaderCarrier = HeaderCarrier()
    lazy val auditService: AuditService = new AuditService(MockAppConfig, mockAuditConnector)
    val PID: String = "SomeonePID"
    val processID: String = "ext90002"
    val processTitle: String = "A process title"
    val submissionTime: DateTime = new DateTime(2020, 4, 23, 13, 0, 0)

    val eventUUID: String = UUID.randomUUID().toString

    def tagsData(name: String, path: Option[String] = None): Map[String, String] =
      Map(
        "clientIP" -> "-",
        "path" -> s"${path.getOrElse("-")}",
        "X-Session-ID" -> "-",
        "Akamai-Reputation" -> "-",
        "X-Request-ID" -> "-",
        "deviceID" -> "-",
        "clientPort" -> "-",
        "transactionName" -> name
      )
    val twoEyeReviewCompleteEvent: TwoEyeReviewCompleteEvent = TwoEyeReviewCompleteEvent(PID, processID, processTitle)
    val factCheckCompleteEvent: FactCheckCompleteEvent = FactCheckCompleteEvent(PID, processID, processTitle)
    val publishedEvent: PublishedEvent = PublishedEvent(PID, processID, processTitle)
  }

  "The Audit service" should {

    "Accept an TwoEyeReviewCompleteEvent object" in new Test {
      val details: JsValue = Json.toJson(twoEyeReviewCompleteEvent)
      val path: Option[String] = Some("/guidance/scratch")
      val extendedEvent: ExtendedDataEvent = ExtendedDataEvent(
        "manage-external-guidance-frontend",
        "2iReviewCompleted",
        eventUUID,
        tagsData("2iReviewCompleted", path),
        details,
        submissionTime
      )
      MockAuditConnector.sendExtendedEvent(extendedEvent)

      auditService.audit(twoEyeReviewCompleteEvent, path)

    }

    "Accept an FactCheckCompleteEvent object" in new Test {
      val details: JsValue = Json.toJson(factCheckCompleteEvent)
      val path: Option[String] = Some("/guidance/approve")
      val extendedEvent: ExtendedDataEvent = ExtendedDataEvent(
        "manage-external-guidance-frontend",
        "factCheckComplete",
        eventUUID,
        tagsData("factCheckComplete", path),
        details,
        submissionTime
      )
      MockAuditConnector.sendExtendedEvent(extendedEvent)

      auditService.audit(factCheckCompleteEvent, path)

    }

    "Accept an PublishedEvent object" in new Test {
      val details: JsValue = Json.toJson(publishedEvent)
      val path: Option[String] = Some("/guidance/publish")
      val extendedEvent: ExtendedDataEvent =
        ExtendedDataEvent("manage-external-guidance-frontend", "published", eventUUID, tagsData("published", path), details, submissionTime)
      MockAuditConnector.sendExtendedEvent(extendedEvent)

      auditService.audit(publishedEvent, path)

    }

  }
}
