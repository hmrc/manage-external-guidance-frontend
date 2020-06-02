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

package models

import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait ProcessStatus

sealed trait ApprovalStatus extends ProcessStatus

object ApprovalStatus extends Enumerable.Implicits {

  case object SubmittedFor2iReview extends WithName("SubmittedFor2iReview") with ApprovalStatus
  case object SubmittedForFactCheck extends WithName("SubmittedForFactCheck") with ApprovalStatus
  case object WithDesignerForUpdate extends WithName("WithDesignerForUpdate") with ApprovalStatus
  case object ApprovedForPublishing extends WithName("ApprovedForPublishing") with ApprovalStatus

  val values: Seq[ApprovalStatus] = Seq(SubmittedFor2iReview, SubmittedForFactCheck, WithDesignerForUpdate, ApprovedForPublishing)

  implicit val enumerable: Enumerable[ApprovalStatus] = Enumerable(values.map(v => v.toString -> v): _*)

  def get2iReviewResultRadioItems(form: Form[_])(implicit messages: Messages): Seq[RadioItem] = {

    Seq(getSend2iReviewResponsesToDesignerRadioItem(form), getApprovedForPublishingRadioItem(form))
  }

  private def getSend2iReviewResponsesToDesignerRadioItem(form: Form[_])(implicit messages: Messages): RadioItem = {

    RadioItem(
      value = Some(WithDesignerForUpdate.toString),
      content = Text(messages(s"2iReviewResult.${WithDesignerForUpdate.toString}")),
      checked = form("value").value.contains(WithDesignerForUpdate.toString)
    )

  }

  private def getApprovedForPublishingRadioItem(form: Form[_])(implicit messages: Messages): RadioItem = {

    RadioItem(
      value = Some(ApprovedForPublishing.toString),
      content = Text(messages(s"2iReviewResult.${ApprovedForPublishing.toString}")),
      checked = form("value").value.contains(ApprovedForPublishing.toString)
    )
  }
}
