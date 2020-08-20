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

package views

import org.jsoup.nodes.Document

import play.twirl.api.HtmlFormat
import views.html.unauthorized_review_error_template

class UnauthorizedReviewErrorViewSpec extends ViewSpecBase {

  val view: unauthorized_review_error_template = injector.instanceOf[unauthorized_review_error_template]

  def createView: (String, String, String) => HtmlFormat.Appendable =
    (pageTitle, headingKey, messageKey) => view(pageTitle, headingKey, messageKey)

  private trait Test {

    val html: HtmlFormat.Appendable = createView("error.unauthorized401.pageTitle.page", "error.unauthorized401.heading.page", "error.unauthorized401.message")

    val doc: Document = asDocument(html)
  }

  "Unauthorized review error template" should {

    "Display the correct page title in the browser for an unauthorized user" in new Test {
      Option(doc.getElementsByTag("title").first).fold(fail("Missing title")) { title =>
        title.text shouldBe s"${messages("error.unauthorized401.pageTitle.page")}${titleSuffix()}"
      }
    }

    "Display the correct page heading for an unauthorised user" in new Test {

      assertPageHeadingEqualsMessage(doc, "error.unauthorized401.heading.page")
    }

    "Display the correct message for an unauthorised user" in new Test {

      assertParagraphTextEqualsMessage(doc, 2, "error.unauthorized401.message")
    }

    "Render a back button" in new Test {

      checkTextOnElementById(doc, "back-link", "unauthorizedReview.backLink")
      checkAttributeOnElementById(doc, "back-link", "class", "govuk-back-link")
      checkAttributeOnElementById(doc, "back-link", "href", "/external-guidance")
    }

  }
}
