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

package forms.mappings

import models.ApprovalStatus

import play.api.data.validation.{Invalid, Valid}

import base.BaseSpec

class ConstraintsSpec extends BaseSpec with Constraints {

  private trait Test {

    val allowedStatuses: Seq[ApprovalStatus] = Seq(ApprovalStatus.WithDesignerForUpdate, ApprovalStatus.WithDesignerForUpdate)

    lazy val containsConstraint = contains(allowedStatuses, "error")
  }

  "The constraints trait" should {

    "Validate a process status is in the list of allowed statuses for a specific task" in new Test {

      containsConstraint(ApprovalStatus.WithDesignerForUpdate) shouldBe Valid
    }

    "Mark a status as invalid when the current status is not appropriate for the task" in new Test {

      containsConstraint(ApprovalStatus.SubmittedFor2iReview) shouldBe Invalid("error")
    }
  }
}