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
import org.jsoup.select.Elements
import play.twirl.api.HtmlFormat
import views.html.twoeye_confirm_error

class TwoEyeConfirmErrorViewSpec extends ViewSpecBase {

  private trait Test {

    val processId = "ext90003"

    val view: twoeye_confirm_error = injector.instanceOf[twoeye_confirm_error]

    def createView: String => HtmlFormat.Appendable = processId => view(processId)

    val doc: Document = asDocument(createView(processId))
  }

  "Following a GET request 2i review confirm error" should {

    "include a back link" in new Test {
      checkTextOnElementById(doc, "back-link", "backlink.label")
      checkAttributeOnElementById(doc, "back-link", "class", "govuk-back-link")
      checkAttributeOnElementById(doc, "back-link", "href", s"/external-guidance/2i-review/$processId")
    }

    "display the expected h1 tag" in new Test {
      checkTextOnElementById(doc, "2i-error-heading", "2iReviewError.heading")
    }

    "display the expected p tag" in new Test {
      checkTextOnElementById(doc, "2i-error-info", "2iReviewError.info")
    }

    "display the back to 2i review button" in new Test {
      checkTextOnElementById(doc, "2i-error-link", "2iReviewError.back")
      checkAttributeOnElementById(doc, "2i-error-link", "role", "button")
      checkAttributeOnElementById(doc, "2i-error-link", "data-module", "govuk-button")
    }
  }

}
