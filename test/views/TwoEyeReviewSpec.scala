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
import java.time._
import org.jsoup._
import views.html._
import org.jsoup.nodes._
import scala.collection.JavaConverters._
import config.AppConfig
import models._
import PageReviewStatus._

class TwoEyeReviewSpec extends ViewSpecBase {

  def elementAttrs(el: Element): Map[String, String] = el.attributes.asScala.toList.map(attr => (attr.getKey, attr.getValue)).toMap

  trait Test {
    private def injector: Injector = app.injector
    def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

    implicit val fakeRequest = FakeRequest("GET", "/")
    implicit def messages: Messages = messagesApi.preferred(Seq(Lang("en")))
    implicit def appconfig: AppConfig = injector.instanceOf[AppConfig]
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
    "Render a page containing all listing all of files for review" in new Test {

      val doc = asDocument(twoEyeReview(approvalProcessReview))

      //val pageLinks = doc.getElementsByTag("a").asScala.filter(elementAttrs(_)("class") == "app-task-list__task-name").map(_.text)
      val pages = doc.getElementsByTag("a").asScala
                     .filter(elementAttrs(_).get("class") == Some("app-task-list__task-name"))
                     .map(_.text)
                     .toList
      approvalProcessReview.pages.forall(p => pages.contains(p.title)) shouldBe true

    }
  }


}

