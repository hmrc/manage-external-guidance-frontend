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

package controllers.actions

import base.ControllerBaseSpec
import controllers.ProcessAdminController
import play.api.mvc.{Action, AnyContent, AnyContentAsEmpty}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import scala.concurrent.ExecutionContext.Implicits.global

class AuthorisedActionSpec extends ControllerBaseSpec {

  val authActions = new AuthAction(stubMessagesControllerComponents().parsers)

  object TestController extends FrontendController(stubMessagesControllerComponents()) {
    private val block = (request: UserRequest[AnyContent]) => Ok(s"Success for ${request.name}")

    def action: Action[AnyContent] = authActions(block)
  }

  val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/some/uri/path")

  "AuthorisedAction" when {
    behave like anAuthActionWith(TestController.action())
  }

  def anAuthActionWith(action: Action[AnyContent]): Unit = {
    "perform the action for an authenticated user" in {
      val someUser = "aUser"
      val result   = action(request.withSession(ProcessAdminController.userSessionKey -> someUser))

      status(result)          shouldBe OK
      contentAsString(result) shouldBe "Success for " + someUser
    }

    s"redirect to sign-in for an unauthenticated user" in {
      val result = action(request)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(
        s"/external-guidance/admin/sign-in")
    }
  }
}
