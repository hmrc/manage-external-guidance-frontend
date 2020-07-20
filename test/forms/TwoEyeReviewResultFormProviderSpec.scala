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

import base.BaseSpec
import models.ApprovalStatus
import play.api.data.{Form, FormError}

class TwoEyeReviewResultFormProviderSpec extends BaseSpec {

  private trait Test {

    val formProvider = new TwoEyeReviewResultFormProvider()

    val form: Form[ApprovalStatus] = formProvider()

    val fieldName = "value"

    val requiredKeyError = "2iReviewResult.error.required"
    val requiredKeyFormError = FormError(fieldName, requiredKeyError)

    val invalidResultType = "invalidResultType"
    val invalidValueError = "error.invalid"
    val invalidFormError = FormError(fieldName, invalidValueError)

    val emptyFormData = Map[String, String]()

    val blank = ""
  }

  "The two eye review result form provider" should {

    "bind all valid values" in new Test {

      val values: Seq[ApprovalStatus] = Seq(ApprovalStatus.Complete, ApprovalStatus.Published)

      for (value <- values) {

        val boundForm: Form[ApprovalStatus] = form.bind(Map(fieldName -> value.toString))

        val result = boundForm.apply(fieldName)

        result.value shouldBe Some(value.toString)
      }
    }

    "not bind an invalid value" in new Test {

      val boundForm: Form[ApprovalStatus] = form.bind(Map(fieldName -> invalidResultType))

      val result = boundForm.apply(fieldName)

      result.errors shouldEqual Seq(invalidFormError)
    }

    "not create a binding when no value is submitted" in new Test {

      val boundForm: Form[ApprovalStatus] = form.bind(emptyFormData)

      val result = boundForm.apply(fieldName)

      result.errors.headOption shouldEqual Some(requiredKeyFormError)
    }

    "not create a binding when the submitted value is a blank string" in new Test {

      val boundForm: Form[ApprovalStatus] = form.bind(Map(fieldName -> blank))

      val result = boundForm.apply(fieldName)

      result.errors.headOption shouldEqual Some(requiredKeyFormError)
    }
  }
}
