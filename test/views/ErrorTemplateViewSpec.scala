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
import views.html.error_template

class ErrorTemplateViewSpec extends ViewSpecBase {

  val view = injector.instanceOf[error_template]

  def createView: (String, String, String) => HtmlFormat.Appendable =
    (pageTitleKey, headingKey, messageKey) => view(pageTitleKey, headingKey, messageKey)

  private trait Test {

    val html = createView("error.unauthorized401.pageTitle", "error.unauthorized401.heading", "error.unauthorized401.message")

    val doc: Document = asDocument(html)

  }

  "Error template view" should {

    "Display the correct page title in the browser for an unauthorized user" in new Test {
      Option(doc.getElementsByTag("title").first).fold(fail("Missing title")){ title =>
        title.text shouldBe s"${messages("error.unauthorized401.pageTitle")}${titleSuffix()}"
      }
    }

    "Display the correct page heading for an unauthorised user" in new Test {

      assertPageHeadingEqualsMessage(doc, "error.unauthorized401.heading")
    }

    "Display the correct message for an unauthorised user" in new Test {

      assertParagraphTextEqualsMessage(doc, 2, "error.unauthorized401.message")
    }

  }
}
