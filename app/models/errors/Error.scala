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

package models.errors

import play.api.libs.json.{Json, OFormat}

case class ProcessError(message: String, stanzaId: String)

object ProcessError {
  implicit val formats: OFormat[ProcessError] = Json.format[ProcessError]
}

case class Error(code: String, message: Option[String], messages: Option[List[ProcessError]])

object Error {
  def apply(code: String, message: String): Error = Error(code, Some(message), None)
  def apply(messages: List[ProcessError] ): Error = Error("UNSUPPORTABLE_ENTITY", None, Some(messages))

  implicit val formats: OFormat[Error] = Json.format[Error]

}

object InvalidProcessError extends Error("BAD_REQUEST", Some("The input process is invalid"), None)

object InternalServerError extends Error("INTERNAL_SERVER_ERROR", Some("An unexpected error has occurred"), None)

object NotFoundError extends Error("NOT_FOUND_ERROR", Some("The resource requested could not be found."), None)

object StaleDataError extends Error("STALE_DATA_ERROR", Some("The resource requested has been changed elsewhere."), None)

object MalformedResponseError extends Error("BAD_REQUEST", Some("The response received could not be parsed"), None)

object BadRequestError extends Error("BAD_REQUEST_ERROR", Some("The request is invalid."), None)

object IncompleteDataError
  extends Error(
    "INCOMPLETE_DATA_ERROR",
    Some("Data is not in the required state for the requested action."), 
    None
  )
