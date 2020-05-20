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

import java.time._
import views.html._
import org.jsoup.nodes._
import scala.collection.JavaConverters._
import models._
import PageReviewStatus._

class TwoEyeReviewSpec extends ViewSpecBase {

  def elementAttrs(el: Element): Map[String, String] = el.attributes.asScala.toList.map(attr => (attr.getKey, attr.getValue)).toMap

  trait Test {
    def twoEyeReview: twoeye_content_review = injector.instanceOf[twoeye_content_review]
    val approvalProcessReview = ApprovalProcessReview(
        "oct9005",
        "Telling HMRC about extra income",
        LocalDate.of(2020, 5, 10),
        List(PageReview("id1", "how-did-you-earn-extra-income", Complete),
          PageReview("id2", "sold-goods-or-services/did-you-only-sell-personal-possessions", NotStarted),
          PageReview("id3", "sold-goods-or-services/have-you-made-a-profit-of-6000-or-more", NotStarted),
          PageReview("id4", "sold-goods-or-services/have-you-made-1000-or-more", NotStarted),
          PageReview("id5", "sold-goods-or-services/you-do-not-need-to-tell-hmrc", NotStarted),
          PageReview("id6", "rent-a-property/do-you-receive-any-income", NotStarted),
          PageReview("id7", "rent-a-property/have-you-rented-out-a-room", NotStarted)))

  }

  "2i Review page" should {
    "Render a page should display process title as the heading" in new Test {

      val doc = asDocument(twoEyeReview(approvalProcessReview))
      doc.getElementsByTag("h1").asScala.filter(elementAttrs(_).get("class") == Some("govuk-heading-xl")).toList match {
        case Nil => fail("Missing H1 heading of the correct class")
        case x :: xs if x.text == approvalProcessReview.title => succeed
        case _ => fail("Heading does not match the title of the process under approval")
      }
    }

    "Render a page containing two sections, one of pages and another with a send confirmation" in new Test {

      val doc = asDocument(twoEyeReview(approvalProcessReview))
      Option(doc.getElementsByTag("ol").first).fold(fail("Missing ordered list elem (ol)")){ ol =>
        val h2s = ol.getElementsByTag("h2").asScala.toList
        h2s.size shouldBe 2
        h2s(0).text shouldBe s"1. ${messages("2iReview.reviewPagesHeading")}"
        h2s(1).text shouldBe s"2. ${messages("2iReview.pagesReviewedConfirmationHeading")}"

        val uls = ol.getElementsByTag("ul").asScala.toList
        uls.size shouldBe 2
        uls(1).text shouldBe messages("2iReview.sendConfirmation")
      }
    }

    "Include a back link" in new Test {
      val doc = asDocument(twoEyeReview(approvalProcessReview))
      Option(doc.getElementsByTag("main").first).fold(fail("Missing main tag")){ main =>
        Option(main.getElementsByTag("a").first).fold(fail("No links in main")){ link =>
          link.text shouldBe messages("backlink.label")
          val attrs = elementAttrs(link)
          attrs.get("class").fold(fail("Missing class attribute on back link")){ clss =>
            clss shouldBe "govuk-back-link"
          }
          attrs.get("href").fold(fail("Missing href attribute on back link")){ href =>
            href shouldBe "/external-guidance/process/approval"
          }
        }
      }
    }

    "Render a page containing all listing all of files and their status" in new Test {

      val doc = asDocument(twoEyeReview(approvalProcessReview))
      Option(doc.getElementsByTag("ul").first).fold(fail("Missing ul element")){ ul =>
        val listItems = ul.getElementsByTag("li").asScala
                          .filter(elementAttrs(_).get("class") == Some("app-task-list__item")).toList

        listItems.foreach{ li =>
          val a = li.getElementsByTag("a").first
          elementAttrs(a).get("aria-describedby").fold(fail("Missing aria-describedby on file link")){ statusId =>
            val url = a.text
            approvalProcessReview.pages.find(_.title == url).fold(fail(s"Missing page with url $url")){ page =>
              messages(s"pageReviewStatus.${page.status.toString}") shouldBe li.getElementById(statusId).text
            }
          }
        }
      }
    }
  }

}

