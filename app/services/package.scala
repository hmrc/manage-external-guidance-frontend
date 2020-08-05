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

import models.ocelot._
import models.errors._

package object services {

  def toProcessError(flowError: FlowError): ProcessError = flowError match {
    case UnknownStanzaType(unknown) => ProcessError(s"Unsupported stanza $unknown found at id = ??", "")
    case StanzaNotFound(id) => ProcessError(s"Missing stanza at id = $id", id)
    case PageStanzaMissing(id) => ProcessError(s"PageSanza expected but missing at id = $id", id)
    case PageUrlEmptyOrInvalid(id) => ProcessError(s"PageStanza URL empty or invalid at id = $id", id)
    case PhraseNotFound(index) => ProcessError(s"Referenced phrase at index $index on stanza id = ?? is missing", "")
    case LinkNotFound(index) => ProcessError(s"Referenced link at index $index on stanza id = ?? is missing" , "")
    case DuplicatePageUrl(id, url) => ProcessError(s"Duplicate page url $url found on stanza id = $id", id)
    case MissingWelshText(index, english) => ProcessError(s"Welsh text at index $index on stanza id = ?? is empty", "")
  }

  def toError(flowErrors: List[FlowError]): Error = Error(flowErrors.map(toProcessError).toList)

}