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
import mocks.MockAppConfig

class PageTitleSpec extends ViewSpecBase {

  "Page rendering" should {

    "Render correct approvals approval_summary_list title" in {
      val view = injector.instanceOf[approval_summary_list]
      val doc = asDocument(view(Nil)(FakeRequest("GET", "/blah"), messages))
      Option(doc.getElementsByTag("h1").first).fold(fail("Missing H1")){ h1 =>
        Option(doc.getElementsByTag("title").first).fold(fail("Missing title")){ title =>
          title.text shouldBe s"${h1.text} - ${MockAppConfig.appName} - ${messages("service.govuk")}" 
        }
      }
    }

    "Render correct accessibility title" in {
      val view = injector.instanceOf[accessibility_statement]
      val doc = asDocument(view()(FakeRequest("GET", "/blah"), messages))
      Option(doc.getElementsByTag("h1").first).fold(fail("Missing H1")){ h1 =>
        Option(doc.getElementsByTag("title").first).fold(fail("Missing title")){ title =>
          title.text shouldBe s"${h1.text} - ${MockAppConfig.appName} - ${messages("service.govuk")}" 
        }
      }
    }

    "Render correct fact check complete title" in {
      val view = injector.instanceOf[fact_check_complete]
      val doc = asDocument(view()(FakeRequest("GET", "/blah"), messages))
      Option(doc.getElementsByTag("h1").first).fold(fail("Missing H1")){ h1 =>
        Option(doc.getElementsByTag("title").first).fold(fail("Missing title")){ title =>
          title.text shouldBe s"${h1.text} - ${MockAppConfig.appName} - ${messages("service.govuk")}" 
        }
      }
    }

    "Render correct fact check confirm error title" in {
      val view = injector.instanceOf[fact_check_confirm_error]
      val doc = asDocument(view("error")(FakeRequest("GET", "/blah"), messages))
      Option(doc.getElementsByTag("h1").first).fold(fail("Missing H1")){ h1 =>
        Option(doc.getElementsByTag("title").first).fold(fail("Missing title")){ title =>
          title.text shouldBe s"${h1.text} - ${MockAppConfig.appName} - ${messages("service.govuk")}" 
        }
      }
    }

  }
}