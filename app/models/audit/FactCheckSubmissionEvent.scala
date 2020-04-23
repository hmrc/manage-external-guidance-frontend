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

import org.joda.time.format.DateTimeFormat
import org.joda.time.LocalDate
import play.api.libs.json.{JsValue, Json, Writes}
import utils.JsonObjectSugar

case class FactCheckSubmissionEvent(PID: String, processID: String, submittedDate: LocalDate, processTitle: String) extends AuditEvent {
  override val transactionName: String = "submittedForFactCheck"
  override val detail: JsValue = Json.toJson(this)
  override val auditType: String = "submittedForFactCheck"    
}

object FactCheckSubmissionEvent extends JsonObjectSugar {
  val dateFormatter = DateTimeFormat.forPattern("YYYY-MM-dd")

  implicit val writes: Writes[FactCheckSubmissionEvent] = Writes { event =>
    jsonObjNoNulls(
      "PID" ->event.PID,
      "processID" -> event.processID,
      "submittedDate" -> event.submittedDate.toString(dateFormatter),
      "processTitle" -> event.processTitle
    )
  }
}