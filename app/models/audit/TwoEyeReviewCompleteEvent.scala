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

package models.audit

import org.joda.time.LocalDate
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import play.api.libs.json.{JsValue, Json, Writes}
import utils.JsonObjectSugar

case class TwoEyeReviewCompleteEvent(auditInfo: AuditInfo) extends AuditEvent {
  val submittedDate: LocalDate = LocalDate.now
  override val transactionName: String = "2iReviewCompleted"
  override val detail: JsValue = Json.toJson(this)
  override val auditType: String = "2iReviewCompleted"
}

object TwoEyeReviewCompleteEvent extends JsonObjectSugar {

  val dateFormatter: DateTimeFormatter = DateTimeFormat.forPattern("YYYY-MM-dd")

  implicit val writes: Writes[TwoEyeReviewCompleteEvent] = Writes { event =>
    jsonObjNoNulls(
      "PID" -> event.auditInfo.pid,
      "processID" -> event.auditInfo.processId,
      "submittedDate" -> event.submittedDate.toString(dateFormatter),
      "processTitle" -> event.auditInfo.processTitle,
      "processVersion" -> event.auditInfo.processVersion,
      "ocelotAuthor" -> event.auditInfo.ocelotAuthor,
      "ocelotLastUpdate" -> event.auditInfo.ocelotLastUpdate,
      "ocelotVersion" -> event.auditInfo.ocelotVersion
    )
  }
}
