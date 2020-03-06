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

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Error( code: String, message: String )

object Error {

  implicit val errorWrites: Writes[Error] = new Writes[Error] {

    def writes( error: Error ) : JsObject = Json.obj(
      "code" -> error.code,
      "message" -> error.message
    )
  }

  implicit val errorReads: Reads[Error] = (
    ( __ \ "code" ).read[String] and
      ( __ \ "message").read[String]
  )(Error.apply _)

}

object ExternalGuidanceServiceError
    extends Error( "INTERNAL_SERVER_ERROR", "An error occurred connecting to the External Guidance service" )
