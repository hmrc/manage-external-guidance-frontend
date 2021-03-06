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

package views

import forms.TwoEyeReviewResultFormProvider
import models.ApprovalStatus
import play.api.data.{Form, FormError}
import play.twirl.api.HtmlFormat
import views.html.twoeye_review_result

import scala.collection.JavaConverters._

class TwoEyeReviewResultViewSpec extends ViewSpecBase {

  private trait Test {

    val processId = "ext90003"

    val formProvider = new TwoEyeReviewResultFormProvider()

    val form: Form[ApprovalStatus] = formProvider()

    val view = injector.instanceOf[twoeye_review_result]

    def createView: (String, Form[ApprovalStatus]) => HtmlFormat.Appendable =
      (processId, form) => view(processId, form)

    val fieldName = "value"

    val requiredKeyError = "2iReviewResultType.error.required"
    val requiredKeyFormError = FormError(fieldName, requiredKeyError)

    val emptyFormData = Map[String, String]()

    val invalidValueError = "error.invalid"
    val invalidValueFormError = FormError(fieldName, invalidValueError)
  }

  "Following a GET request 2i review" should {

    "include a back link" in new Test {

      val doc = asDocument(createView(processId, form))

      checkTextOnElementById(doc, "back-link", "backlink.label")
      checkAttributeOnElementById(doc, "back-link", "class", "govuk-back-link")
      checkAttributeOnElementById(doc, "back-link", "href", s"/external-guidance/2i-review/$processId")
    }

    "display the appropriate radio buttons" in new Test {

      val doc = asDocument(createView(processId, form))

      val inputs = doc.getElementsByTag("input").asScala.toList

      val radios = inputs.filter(input =>
        elementAttrs(input).get("type").fold(false) { inputType =>
          inputType == "radio"
        }
      )

      radios.size shouldBe 2

      elementAttrs(radios.head).get("class").fold(fail("Missing class attribute on first radio button")) { clss =>
        clss shouldBe "govuk-radios__input"
      }

      elementAttrs(radios.head).get("value").fold(fail("Missing value on first radio button ")) { value =>
        value shouldBe ApprovalStatus.Complete.toString
      }

      elementAttrs(radios.last).get("class").fold(fail("Missing class attribute on second radio button")) { clss =>
        clss shouldBe "govuk-radios__input"
      }

      elementAttrs(radios.last).get("value").fold(fail("Missing value on second radio button ")) { value =>
        value shouldBe ApprovalStatus.Published.toString
      }

      val labels = doc.getElementsByTag("label").asScala.toList

      val radioLabels = labels.filter(label =>
        elementAttrs(label).get("class").fold(false) { clss =>
          clss.contains("govuk-radios__label")
        }
      )

      radioLabels.size shouldBe 2

      radioLabels.head.text shouldBe messages(s"2iReviewResult.${ApprovalStatus.Complete.toString}")
      radioLabels.last.text shouldBe messages(s"2iReviewResult.${ApprovalStatus.Published.toString}")
    }

    "display the complete 2i review button" in new Test {

      val doc = asDocument(createView(processId, form))

      val buttons = doc.getElementsByTag("button")

      buttons.size shouldBe 1

      elementAttrs(buttons.first).get("class").fold(fail("Class on complete review button is not defined")) { clss =>
        clss shouldBe "govuk-button"
      }

      assertTextEqualsMessage(buttons.first.text, "2iReviewResult.completeReview")
    }
  }

  "Following a failed submission 2i review result owing to empty data submission" should {

    "display an error summary" in new Test {

      val boundForm: Form[ApprovalStatus] = form.bind(emptyFormData)

      val doc = asDocument(createView(processId, boundForm))

      val divs = doc.getElementsByTag("div").asScala.toList

      val errorSummary = divs.filter(div =>
        elementAttrs(div).get("class").fold(false) { clss =>
          clss == "govuk-error-summary"
        }
      )

      errorSummary.size shouldBe 1

      val errorSummaryTitle = doc.getElementById("error-summary-title")

      assertTextEqualsMessage(errorSummaryTitle.text, "error.summary.title")

      val errorSummaryList = doc.getElementsByClass("govuk-list govuk-error-summary__list")

      errorSummaryList.isEmpty shouldBe false

      assertTextEqualsMessage(errorSummaryList.text, "2iReviewResult.error.required")
    }

    "display an error message" in new Test {

      val boundForm: Form[ApprovalStatus] = form.bind(emptyFormData)

      val doc = asDocument(createView(processId, boundForm))

      val errorMessageSpan = doc.getElementById("value-error")

      val errorMsg = messages("2iReviewResult.error.required")

      errorMessageSpan.text.replaceAll("\u00a0", " ") shouldBe s"Error: $errorMsg".replaceAll("&nbsp;", " ")
    }

    "Add id of error message span to the aria field of fieldset" in new Test {

      val boundForm: Form[ApprovalStatus] = form.bind(emptyFormData)

      val doc = asDocument(createView(processId, boundForm))

      val fieldSet = doc.getElementsByTag("fieldset").first()

      elementAttrs(fieldSet)("aria-describedby").contains("value-error") shouldBe true
    }

  }

  "Following a failed submission 2i review result owing to invalid data submission" should {

    "display an error summary" in new Test {

      val boundForm: Form[ApprovalStatus] = form.bind(Map(fieldName -> ApprovalStatus.Submitted.toString))

      val doc = asDocument(createView(processId, boundForm))

      val divs = doc.getElementsByTag("div").asScala.toList

      val errorSummary = divs.filter(div =>
        elementAttrs(div).get("class").fold(false) { clss =>
          clss == "govuk-error-summary"
        }
      )

      errorSummary.size shouldBe 1

      val errorSummaryTitle = doc.getElementById("error-summary-title")

      assertTextEqualsMessage(errorSummaryTitle.text, "error.summary.title")

      val errorSummaryList = doc.getElementsByClass("govuk-list govuk-error-summary__list")

      errorSummaryList.isEmpty shouldBe false

      assertTextEqualsMessage(errorSummaryList.text, "2iReviewResult.error.required")
    }

    "display an error message" in new Test {

      val boundForm: Form[ApprovalStatus] = form.bind(Map(fieldName -> ApprovalStatus.Submitted.toString))

      val doc = asDocument(createView(processId, boundForm))

      val errorMessageSpan = doc.getElementById("value-error")

      val errorMsg = messages("2iReviewResult.error.required")

      errorMessageSpan.text.replaceAll("\u00a0", " ") shouldBe s"Error: $errorMsg".replaceAll("&nbsp;", " ")
    }

    "Add id of error message span to the aria field of fieldset" in new Test {

      val boundForm: Form[ApprovalStatus] = form.bind(emptyFormData)

      val doc = asDocument(createView(processId, boundForm))

      val fieldSet = doc.getElementsByTag("fieldset").first()

      elementAttrs(fieldSet)("aria-describedby").contains("value-error") shouldBe true
    }

  }

}
