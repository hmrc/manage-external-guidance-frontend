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

import play.api.inject.Injector
import play.api.i18n.{Messages, MessagesApi, Lang}
import play.api.test.FakeRequest
import play.twirl.api.Html
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.jsoup._
import views.html._
import org.jsoup.nodes._
import scala.collection.JavaConverters._
import config.AppConfig
import models._
import ApprovalStatus._

class ApprovalsListSpec extends ViewSpecBase {

  def elementAttrs(el: Element): Map[String, String] = el.attributes.asScala.toList.map(attr => (attr.getKey, attr.getValue)).toMap

  trait Test {
    private def injector: Injector = app.injector
    def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

    implicit val fakeRequest = FakeRequest("GET", "/")
    implicit def messages: Messages = messagesApi.preferred(Seq(Lang("en")))
    implicit def appconfig: AppConfig = injector.instanceOf[AppConfig]
    def approvalsListView: approval_summary_list = injector.instanceOf[approval_summary_list]
    val summaries = Seq(
      ApprovalProcessSummary("oct9005", "EU exit guidance", LocalDate.of(2020, 5, 4), WithDesignerForUpdate),
      ApprovalProcessSummary("oct9006", "Customer wants to make a cup of tea", LocalDate.of(2020, 5, 4), SubmittedFor2iReview),
      ApprovalProcessSummary("oct9007", "Telling HMRC about extra income", LocalDate.of(2020, 4, 1), WithDesignerForUpdate),
      ApprovalProcessSummary("oct9008", "Find a lost user ID and password", LocalDate.of(2020, 4, 2), SubmittedForFactCheck)
    )
  }

  "Approval Summary List page" should {
    "Render with correct heading" in new Test {

      val doc = asDocument(approvalsListView(summaries))
      doc.getElementsByTag("h1").asScala.filter(elementAttrs(_).get("class") == Some("govuk-heading-xl")).toList match {
        case Nil => fail("Missing H1 heading of the correct class")
        case x :: xs if x.text == messages("approvals.tableTitle") => succeed
        case _ => fail("Heading does not match the title of the process under approval")
      }
    }

    "Contain a table with correct headings" in new Test {

      val doc = asDocument(approvalsListView(summaries))
      Option(doc.getElementsByTag("table").first).fold(fail("Missing table elem")){ table =>
        val ths = table.getElementsByTag("th").asScala.toList
        ths.size shouldBe 3
        ths(0).text shouldBe messages("approvals.processTitle")
        elementAttrs((ths(0))).get("class").fold(fail("Missing class on table col header"))(_ should include ("govuk-table__header"))
        
        ths(1).text shouldBe messages("approvals.dateProcessUpdatedTitle")
        elementAttrs((ths(1))).get("class").fold(fail("Missing class on table col header"))(_ should include ("govuk-table__header"))

        ths(2).text shouldBe messages("approvals.processStatusTitle")
        elementAttrs((ths(2))).get("class").fold(fail("Missing class on table col header"))(_ should include ("govuk-table__header"))
      }
    }

    "include a table entry for each approval summary in order" in new Test {

      val doc = asDocument(approvalsListView(summaries))
      Option(doc.getElementsByTag("tbody").first).fold(fail("Missing table body")){ tbody =>
        val rows = tbody.getElementsByTag("tr").asScala.toList

        rows.size shouldBe summaries.size

        rows.zip(summaries).foreach{
          case (r,s) =>
            val cellData = r.getElementsByTag("td").asScala.map(_.text).toList
            cellData.size shouldBe 3
            cellData(0) == s.title
            cellData(1) == s.lastUpdated.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
            cellData(2) == messages(s"approvalsStatus.${s.status.toString}")
        }
      }
    }
  }


}

