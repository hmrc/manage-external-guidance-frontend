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

package forms

import forms.mappings.{Constraints, Mappings}
import javax.inject.Inject
import models.ApprovalStatus
import play.api.data.Form

class TwoEyeReviewResultFormProvider @Inject() extends Mappings with Constraints {

  def apply(): Form[ApprovalStatus] =
    Form(
      "value" -> enumerable[ApprovalStatus]("2iReviewResult.error.required")
        .verifying(contains[ApprovalStatus](Seq(ApprovalStatus.Complete, ApprovalStatus.Published), "2iReviewResult.error.required"))
    )
}
