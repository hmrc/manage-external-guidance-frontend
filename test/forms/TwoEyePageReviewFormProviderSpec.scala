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

package forms

import base.BaseSpec
import models.YesNoAnswer
import models.forms.TwoEyePageReview
import play.api.data.{Form, FormError}

class TwoEyePageReviewFormProviderSpec extends BaseSpec {

  private trait Test {

    val formProvider = new TwoEyePageReviewFormProvider()

    val form: Form[TwoEyePageReview] = formProvider()

    val answer = "answer"
    val comment = "comment"

    val definedComment = "This page is acceptable"
    val emptyComment = ""

    val requiredAnswerKeyError = "2iPageReview.answer.error.required"
    val requiredAnswerKeyFormError = FormError(answer, requiredAnswerKeyError)

    val requiredCommentKeyError = "2iPageReview.comment.error.required"
    val requiredCommentKeyFormError = FormError(comment, requiredCommentKeyError)
  }

  "The two eye page review form provider" should {

    "bind a valid response and defined comment" in new Test {

      val boundForm: Form[TwoEyePageReview] = form.bind(
        Map(
          answer -> YesNoAnswer.Yes.toString,
          comment -> definedComment
        )
      )

      boundForm.apply(answer).value shouldBe Some(YesNoAnswer.Yes.toString)
      boundForm.apply(comment).value shouldBe Some(definedComment)
    }

    "bind a valid response and empty comment" in new Test {

      val boundForm: Form[TwoEyePageReview] = form.bind(
        Map(
          answer -> YesNoAnswer.No.toString,
          comment -> emptyComment
        )
      )

      boundForm.apply(answer).value shouldBe Some(YesNoAnswer.No.toString)
      boundForm.apply(comment).value shouldBe Some(emptyComment)
    }

    "raise an error when no answer is given" in new Test {

      val boundForm: Form[TwoEyePageReview] = form.bind(
        Map(
          comment -> definedComment
        )
      )

      val result = boundForm.apply(answer)

      result.errors.headOption shouldBe Some(requiredAnswerKeyFormError)
    }

    // "raise an error when the comment is missing" in new Test {

    //   // Note in reality this is unlikely to happen

    //   val boundForm: Form[TwoEyePageReview] = form.bind(
    //     Map(
    //       answer -> YesNoAnswer.Yes.toString
    //     )
    //   )

    //   val result = boundForm.apply(comment)

    //   result.errors.headOption shouldBe Some(requiredCommentKeyFormError)
    // }

    // TODO: Require tests for maximum length of commente when this is known
  }
}
