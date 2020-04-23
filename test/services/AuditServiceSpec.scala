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

import uk.gov.hmrc.http.HeaderCarrier
import models.audit.AuditEvent
import models.ScratchResponse
import base.BaseSpec
import mocks.MockAuditConnector

class AuditServiceSpec extends BaseSpec {

  private trait Test extends MockAuditConnector {
    implicit val headerCarrier: HeaderCarrier = HeaderCarrier()
    lazy val auditService: AuditService = new AuditService(mockAuditConnector)
  }

  "The Audit service" should {

    "Accept an audit type string and an AuditEvent object" in new Test {
      val auditType = "SomeAuditType"
      val eventDescription = "An audit event description"

      MockAuditConnector.sendExplicitAudit(auditType, AuditEvent(eventDescription))

      auditService.audit(auditType, AuditEvent(eventDescription))

    }

    "Accept an audit type string and an event object T with available Writes[T]" in new Test {
      val auditType = "SomeAuditType"
      val submissionResponse = SaveScratchSubmissionResponse("ID")

      MockAuditConnector.sendExplicitAudit(auditType, submissionResponse)

      auditService.audit(auditType, submissionResponse)

    }

  }
}
