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
import models.audit.ApprovedForPublishingEvent
import models.audit._

class AuditServiceSpec extends BaseSpec {

  private trait Test extends MockAuditConnector {
    implicit val headerCarrier: HeaderCarrier = HeaderCarrier()
    lazy val auditService: AuditService = new AuditService(MockAppConfig, mockAuditConnector)
    val PID = "SomeonePID"
    val processID = "ext90002"
    val processTitle = "A process title"
    val submissionTime = new DateTime(2020,4,23,13,0,0)

    val eventUUID = UUID.randomUUID().toString
    def tagsData(name: String): Map[String, String] = Map("clientIP" -> "-", 
                   "path" -> "-", 
                   "X-Session-ID" -> "-", 
                   "Akamai-Reputation" -> "-", 
                   "X-Request-ID" -> "-", 
                   "deviceID" -> "-", 
                   "clientPort" -> "-",
                   "transactionName" -> name)
    val approvalEvent = ApprovedForPublishingEvent(PID, processID, processTitle)
    val factCheckEvent = FactCheckSubmissionEvent(PID, processID, processTitle)
    val readyForPubEvent = ReadyForPublishingEvent(PID, processID, processTitle)
  }

  "The Audit service" should {

    "Accept an ApprovedForPublishingEvent object" in new Test {
      val details = Json.toJson(approvalEvent)  
      val extendedEvent = ExtendedDataEvent("manage-external-guidance-frontend", 
                                            "approvedForPublishing",
                                            eventUUID,
                                            tagsData("approvedForPublishing"),
                                            details,
                                            submissionTime)
      MockAuditConnector.sendExtendedEvent(extendedEvent)

      auditService.audit(approvalEvent)

    }

    "Accept an FactCheckSubmissionEvent object" in new Test {
      val details = Json.toJson(factCheckEvent)  
      val extendedEvent = ExtendedDataEvent("manage-external-guidance-frontend", 
                                            "submittedForFactCheck",
                                            eventUUID,
                                            tagsData("submittedForFactCheck"),
                                            details,
                                            submissionTime)
      MockAuditConnector.sendExtendedEvent(extendedEvent)

      auditService.audit(factCheckEvent)

    }

    "Accept an ReadyForPublishingEvent object" in new Test {
      val details = Json.toJson(readyForPubEvent)  
      val extendedEvent = ExtendedDataEvent("manage-external-guidance-frontend", 
                                            "readyForPublishing",
                                            eventUUID,
                                            tagsData("readyForPublishing"),
                                            details,
                                            submissionTime)
      MockAuditConnector.sendExtendedEvent(extendedEvent)

      auditService.audit(readyForPubEvent)

    }

  }
}
