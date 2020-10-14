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
import views.html.twoeye_complete_error

class TwoEyeCompleteErrorViewSpec extends ViewSpecBase {

  private trait Test {

    val processId = "ext90003"

    val view: twoeye_complete_error = injector.instanceOf[twoeye_complete_error]

    def createView: String => HtmlFormat.Appendable = _ => view()

    val doc: Document = asDocument(createView(processId))
  }

  "Following a duplicate error confirm error" should {
    "display the expected h1 tag" in new Test {
      checkTextOnElementById(doc, "2i-error-heading", "2iReviewCompleteError.heading")
    }

    "display the expected p tags" in new Test {
      checkTextOnElementById(doc, "2i-error-content", "2iReviewCompleteError.reviewContent")
      checkTextOnElementById(doc, "2i-error-info", "2iReviewCompleteError.info")
    }

    "display the back to dashboard button" in new Test {
      checkTextOnElementById(doc, "2i-error-link", "2iReviewComplete.close")
      checkAttributeOnElementById(doc, "2i-error-link", "role", "button")
      checkAttributeOnElementById(doc, "2i-error-link", "data-module", "govuk-button")
    }
  }

}
