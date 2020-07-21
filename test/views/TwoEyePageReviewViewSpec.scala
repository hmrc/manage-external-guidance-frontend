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

import forms.TwoEyePageReviewFormProvider

import views.html.twoeye_page_review

import scala.collection.JavaConverters._

class TwoEyePageReviewViewSpec extends ViewSpecBase {

  trait Test {

    val processId = "ext00001"
    val reviewUrl = "/example"
    val reviewTitle = "Example page"

    val formProvider: TwoEyePageReviewFormProvider = new TwoEyePageReviewFormProvider()
    val form = formProvider()

    val two_eye_page_review = app.injector.instanceOf[twoeye_page_review]

    val doc = asDocument(two_eye_page_review(processId, reviewUrl, reviewTitle, form, 0))
  }

  "The rendered two eye page review" should {

    "contain a link to the page under review" in new Test {

      Option(doc.getElementsByClass("govuk-grid-row").first).fold(fail("Unable to find govuk-grid-row")) { div =>
        val linksList = div.getElementsByTag("a").asScala.toList

        linksList.size shouldBe >(1)

        elementAttrs(linksList(1))("href") should endWith(s"/guidance-review/approval/$processId$reviewUrl")

        linksList(1).text shouldBe messages("2iPageReview.viewGuidancePage")
      }
    }

    "contain a legend with visually hidden text" in new Test {

      Option(doc.getElementsByTag("legend").first).fold(fail("Unable to locate legend element")) { legend =>
        elementAttrs(legend)("class") shouldBe "govuk-visually-hidden"

        val textComponents: Seq[String] = Seq(messages("does.the.page"), reviewTitle, messages("meet.govuk.standards"))

        legend.text shouldBe textComponents.mkString(" ")
      }
    }

  }
}