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

package views.process_admin

import models._
import org.jsoup.nodes.Document
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.process_admin._

import java.time.{Instant, ZonedDateTime}
import models.admin._

class ProcessListSpec extends views.ViewSpecBase {

  trait Test {
    def approvals: approval_summaries = injector.instanceOf[approval_summaries]
    def published: published_summaries = injector.instanceOf[published_summaries]
    def archived: archived_summaries = injector.instanceOf[archived_summaries]
    def active: active_summaries = injector.instanceOf[active_summaries]

    val PageUrls: Map[Page, String] = Map(
      PublishedList -> s"/external-guidance${controllers.routes.ProcessAdminController.listPublished.url}",
      ApprovalsList -> s"/external-guidance${controllers.routes.ProcessAdminController.listApprovals.url}",
      ArchivedList -> s"/external-guidance${controllers.routes.ProcessAdminController.listArchived.url}",
      ActiveList -> s"/external-guidance${controllers.routes.ProcessAdminController.listActive.url}",
    )

    val summaries = List(ProcessSummary("id", "processCode", 1, "author", None, ZonedDateTime.now, "actionedby", "Status"))
    val activeSummaries = List(CachedProcessSummary("id", 123456789L, None, None, "process title", Instant.now))

    implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")
  }

  "Admin pages" should {

    "Render list of approvals" in new Test {

      val doc: Document = asDocument(approvals(summaries, PageUrls, controllers.routes.ProcessAdminController.getApproval _)(fakeRequest, messages))

      doc.toString.contains("Approval Processes") shouldBe true
    }

    "Render list of published" in new Test {

      val doc: Document = asDocument(published(summaries, PageUrls, controllers.routes.ProcessAdminController.getPublished _)(fakeRequest, messages))
      doc.toString.contains("Published Processes") shouldBe true
    }

    "Render list of archived" in new Test {

      val doc: Document = asDocument(archived(summaries, PageUrls, controllers.routes.ProcessAdminController.getArchived _)(fakeRequest, messages))
      doc.toString.contains("Archived Processes") shouldBe true
    }

    "Render list of active cached processes" in new Test {

      val doc: Document = asDocument(active(activeSummaries, PageUrls, controllers.routes.ProcessAdminController.getActive _)(fakeRequest, messages))
      doc.toString.contains("Active Processes") shouldBe true
    }

  }
}
