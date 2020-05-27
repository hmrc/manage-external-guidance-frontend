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

package models.forms

import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import models.{Enumerable, WithName}

sealed trait TwoEyeReviewResultType

object TwoEyeReviewResultType extends Enumerable.Implicits {

  case object Send2iReviewResponsesToDesigner extends WithName("send2iReviewResponsesToDesigner") with TwoEyeReviewResultType
  case object SendGuidanceToFactChecker extends WithName("sendGuidanceToFactChecker") with TwoEyeReviewResultType
  case object ApproveGuidanceForPublishing extends WithName("approveGuidanceForPublishing") with TwoEyeReviewResultType

  val values: Seq[TwoEyeReviewResultType] = Seq(
    Send2iReviewResponsesToDesigner,
    SendGuidanceToFactChecker,
    ApproveGuidanceForPublishing
  )

  def options(form: Form[_])(implicit messages: Messages): Seq[RadioItem] =
    values.map { value =>
      RadioItem(
        value = Some(value.toString),
        content = Text(messages(s"2iReviewResultType.${value.toString}")),
        checked = form("value").value.contains(value.toString)
      )
    }

  implicit val enumerable: Enumerable[TwoEyeReviewResultType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
