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

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import models.ApprovalStatus._
import models._
import views.html._
import controllers.routes
import play.api.test.FakeRequest
import scala.collection.JavaConverters._

class ApprovalsListSpec extends ViewSpecBase {

  trait Test {
    def approvalsListView: approval_summary_list = injector.instanceOf[approval_summary_list]

    val summaries = Seq(
      ApprovalProcessSummary("oct9005", "EU exit guidance", LocalDate.of(2020, 5, 4), WithDesignerForUpdate),
      ApprovalProcessSummary("oct9006", "Customer wants to make a cup of tea", LocalDate.of(2020, 5, 4), SubmittedFor2iReview),
      ApprovalProcessSummary("oct9007", "Telling HMRC about extra income", LocalDate.of(2020, 4, 1), WithDesignerForUpdate),
      ApprovalProcessSummary("oct9008", "Find a lost user ID and password", LocalDate.of(2020, 4, 2), SubmittedForFactCheck)
    )

    implicit val fakeRequest = FakeRequest("GET", "/process/approval")

    val doc = asDocument(approvalsListView(summaries)(fakeRequest, messages))
  }

  "Approval Summary List page" should {

    "Render header with service name link" in new Test {

      val headers = doc.getElementsByClass("govuk-header__content")

      headers.size() shouldBe 1

      Option(headers.first.getElementsByTag("a").first).fold(fail("Service Url link not found")) { a =>
        a.text shouldBe messages("service.name")

        elementAttrs(a).get("href").fold(fail("Missing href attribute in anchor")) { href =>
          href shouldBe routes.AdminController.approvalSummaries().url
        }
      }
    }

    "Render with correct heading" in new Test {
      Option(doc.getElementsByTag("h1").first).fold(fail("Missing H1 heading of the correct class")){h1 =>
        elementAttrs(h1)("class") should include("govuk-heading-xl")
        h1.text shouldBe messages("approvals.tableTitle")
      }
    }

    "Contain a table with correct headings" in new Test {

      Option(doc.getElementsByTag("table").first).fold(fail("Missing table elem")) { table =>
        val ths = table.getElementsByTag("th").asScala.toList
        ths.size shouldBe 3
        ths(0).text shouldBe messages("approvals.processTitle")
        elementAttrs(ths(0)).get("class").fold(fail("Missing class on table col header"))(_ should include("govuk-table__header"))

        ths(1).text shouldBe messages("approvals.dateProcessUpdatedTitle")
        elementAttrs(ths(1)).get("class").fold(fail("Missing class on table col header"))(_ should include("govuk-table__header"))

        ths(2).text shouldBe messages("approvals.processStatusTitle")
        elementAttrs(ths(2)).get("class").fold(fail("Missing class on table col header"))(_ should include("govuk-table__header"))
      }
    }

    "define a link to the general comments and feedback page" in new Test {

      Option(doc.getElementsByClass("govuk-grid-column-two-thirds").first).fold(fail("Missing layout grid")) { gridDiv =>
        Option(gridDiv.getElementsByTag("a").first).fold(fail("No anchor elements found in layout grid")) { a =>
          a.text shouldBe messages("approvals.commentsAndFeedback")

          elementAttrs(a).get("class").fold(fail("Missing class attribute on comments and feedback link")) { clss =>
            clss shouldBe "govuk-link"
          }
        }
      }

    }

    "include a table entry for each approval summary in order" in new Test {

      Option(doc.getElementsByTag("tbody").first).fold(fail("Missing table body")) { tbody =>
        val rows = tbody.getElementsByTag("tr").asScala.toList

        rows.size shouldBe summaries.size

        rows.zip(summaries).foreach {
          case (r, s) =>
            val cellData = r.getElementsByTag("td").asScala.toList

            cellData.size shouldBe 3

            s.status match {

              case SubmittedForFactCheck | SubmittedFor2iReview => {

                cellData.head.text shouldBe s.title

                Option(cellData.head.getElementsByTag("a").first).fold(fail("Missing link from page url cell")) { a =>
                  elementAttrs(a).get("href").fold(fail("Missing href attribute within anchor")) { href =>
                    s.status match {
                      case SubmittedFor2iReview => href shouldBe routes.TwoEyeReviewController.approval(s.id).toString
                      case SubmittedForFactCheck => href shouldBe routes.FactCheckController.approval(s.id).toString
                    }
                  }
                }
              }
              case _ => cellData.head.text shouldBe s.title
            }

            cellData(1).text shouldBe s.lastUpdated.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
            cellData(2).text shouldBe messages(s"approvalsStatus.${s.status.toString}")
        }
      }
    }

    "Render link to contact frontend beta feedback page" in new Test {

      Option(doc.getElementById("beta-feedback-link")).fold(fail("Test cannot locate beta feedback link")) { a =>
        a.text shouldBe messages("feedback.linkLabel")

        elementAttrs(a).get("href").fold(fail("Test cannot locate href attribute in beta feedback link")) { href =>
          href shouldBe appConfig.contactFrontendFeedbackUrl
        }
      }
    }

    "Render link to contact frontend page help" in new Test {

      Option(doc.getElementById("getpagehelp-link")).fold(fail("Test cannot locate contact frontend page help link")) { a =>
        a.text shouldBe messages("getPageHelp.linkText")

        elementAttrs(a).get("href").fold(fail("Test cannot locate href attribute in contact frontend page help link")) { href =>
          href shouldBe appConfig.reportAProblemNonJSUrl
        }
      }
    }
  }

}
