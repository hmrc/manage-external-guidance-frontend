/*
 * Copyright 2021 HM Revenue & Customs
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

package models

import java.time.format.{DateTimeFormatter, ResolverStyle}

case class UpdateDisplayDetails(time: String, date: String, credId: String, user: String, email: String, retained: List[String])

object UpdateDisplayDetails {
  val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM uuuu", java.util.Locale.UK).withResolverStyle(ResolverStyle.STRICT)
  val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a", java.util.Locale.UK).withResolverStyle(ResolverStyle.STRICT)

  def apply(d: UpdateDetails): UpdateDisplayDetails =
    UpdateDisplayDetails(d.when.format(timeFormatter), d.when.format(dateFormatter), d.credId, d.user, d.email, d.retainedDeletions)
}
