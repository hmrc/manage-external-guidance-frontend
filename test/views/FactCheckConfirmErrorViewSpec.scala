/*
 * Copyright 2023 HM Revenue & Customs
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
import org.jsoup.select.Elements
import play.twirl.api.HtmlFormat
import views.html.fact_check_confirm_error

class FactCheckConfirmErrorViewSpec extends ViewSpecBase {

  private trait Test {

    val processId = "ext90003"

    val view: fact_check_confirm_error = injector.instanceOf[fact_check_confirm_error]

    def createView: String => HtmlFormat.Appendable = processId => view(processId)

    val doc: Document = asDocument(createView(processId))
  }

  "Following a GET request fact check confirm error" should {

    "include a back link" in new Test {
      checkTextOnElementById(doc, "back-link", "backlink.label")
      checkAttributeOnElementById(doc, "back-link", "class", "govuk-back-link")
      checkAttributeOnElementById(doc, "back-link", "href", s"/external-guidance/fact-check/$processId")
    }

    "display the expected h1 tag" in new Test {
      checkTextOnElementById(doc, "fact-check-error-heading", "factCheckError.heading")
    }

    "display the expected p tag" in new Test {
      checkTextOnElementById(doc, "fact-check-error-info", "factCheckError.info")
    }

    "display the back to fact check button" in new Test {
      checkTextOnElementById(doc, "fact-check-error-link", "factCheckError.back")
      checkAttributeOnElementById(doc, "fact-check-error-link", "role", "button")
      checkAttributeOnElementById(doc, "fact-check-error-link", "data-module", "govuk-button")
    }
  }

}
