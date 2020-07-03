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
import java.util.UUID

import models.PageReviewStatus._
import models.{ApprovalProcessReview, PageReview}
import views.html.fact_check_content_review

import scala.collection.JavaConverters._

class FactCheckReviewSpec extends ViewSpecBase {

  trait Test {

    def factCheckReview: fact_check_content_review = injector.instanceOf[fact_check_content_review]

    val year = 2020
    val month = 5
    val day = 11

    val approvalProcessReview = ApprovalProcessReview(
      UUID.randomUUID().toString,
      "oct9005",
      "Telling HMRC about extra income",
      LocalDate.of(year, month, day),
      List(
        PageReview("id1", "how-did-you-earn-extra-income", Complete),
        PageReview("id2", "sold-goods-or-services/did-you-only-sell-personal-possessions", NotStarted),
        PageReview("id3", "sold-goods-or-services/have-you-made-a-profit-of-6000-or-more", NotStarted),
        PageReview("id4", "sold-goods-or-services/have-you-made-1000-or-more", NotStarted),
        PageReview("id5", "sold-goods-or-services/you-do-not-need-to-tell-hmrc", NotStarted),
        PageReview("id6", "rent-a-property/do-you-receive-any-income", NotStarted),
        PageReview("id7", "rent-a-property/have-you-rented-out-a-room", NotStarted)
      )
    )

    val doc = asDocument(factCheckReview(approvalProcessReview))
  }

  "Fact check review" should {

    "Render a page should display process title as the heading" in new Test {

      doc.getElementsByTag("h1").asScala.filter(elementAttrs(_).get("class") == Some("govuk-heading-xl")).toList match {
        case Nil => fail("Missing H1 heading of the correct class")
        case x :: xs if x.text == approvalProcessReview.title => succeed
        case _ => fail("Heading does not match the title of the process under approval")
      }

    }

    "Render header with caption paragraph" in new Test {

      Option(doc.getElementsByTag("main").first).fold(fail("Unable to locate main element in view")) { main =>
        Option(main.getElementsByTag("header").first).fold(fail("Unable to locate header in main element")) { header =>
          header.children.size shouldBe 2

          header.child(0).tagName shouldBe "h1"

          header.child(1).tagName shouldBe "p"
          header.child(1).text shouldBe messages("factCheck.heading")
        }
      }

    }

  }

}
