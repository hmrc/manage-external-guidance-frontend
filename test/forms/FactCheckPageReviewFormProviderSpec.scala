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
import models.YesNoAnswer
import models.forms.FactCheckPageReview
import play.api.data.{Form, FormError}

class FactCheckPageReviewFormProviderSpec extends BaseSpec {

  private trait Test {

    val formProvider = new FactCheckPageReviewFormProvider()

    val form: Form[FactCheckPageReview] = formProvider()

    val answer = "answer"

    val requiredAnswerKeyError = "factCheckPageReview.answer.error.required"
    val requiredAnswerKeyFormError = FormError(answer, requiredAnswerKeyError)
  }

  "The fact check page review form provider" should {

    "bind a valid response" in new Test {

      val boundForm: Form[FactCheckPageReview] = form.bind(
        Map(
          answer -> YesNoAnswer.Yes.toString
        )
      )

      boundForm.apply(answer).value shouldBe Some(YesNoAnswer.Yes.toString)
    }
  }
}
