/*
 * Copyright 2022 HM Revenue & Customs
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

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import controllers.routes
import models.ApprovalStatus._
import models.ReviewType.{ReviewType2i, ReviewTypeFactCheck}
import models._
import play.api.test.FakeRequest
import views.html.process_admin._

import scala.collection.JavaConverters._

class ProcessListSpec extends views.ViewSpecBase {

  trait Test {
    def approvals: approval_summaries = injector.instanceOf[approval_summaries]
    def published: published_summaries = injector.instanceOf[published_summaries]
    def archived: archived_summaries = injector.instanceOf[archived_summaries]

    val summaries = List(ProcessSummary("id", "processCode", 1, "author", None, ZonedDateTime.now, "actionedby", "Status"))

    implicit val fakeRequest = FakeRequest("GET", "/")
  }

  "Admin pages" should {

    "Render list of approvals" in new Test {

      val doc = asDocument(approvals(summaries)(fakeRequest, messages))

      doc.toString().contains("Approval Processes") shouldBe true
    }

    "Render list of published" in new Test {

      val doc = asDocument(published(summaries)(fakeRequest, messages))
      doc.toString().contains("Published Processes") shouldBe true
    }

    "Render list of archived" in new Test {

      val doc = asDocument(archived(summaries)(fakeRequest, messages))
      doc.toString().contains("Archived Processes") shouldBe true
    }

  }
}
