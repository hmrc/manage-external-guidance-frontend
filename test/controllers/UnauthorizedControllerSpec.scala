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

package controllers

import config.ErrorHandler

import play.api.http.Status

import base.BaseSpec

import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import play.api.test.FakeRequest
import play.api.test.Helpers._

class UnauthorizedControllerSpec extends BaseSpec with GuiceOneAppPerSuite {

  private trait Test {

    val fakeRequest = FakeRequest("GET", "/")

    val errorHandler = app.injector.instanceOf[ErrorHandler]

    val target = new UnauthorizedController(errorHandler, stubMessagesControllerComponents())
  }

  "UnauthorizedController" should {

    "Return status Ok following valid get request" in new Test {

      val result = target.onPageLoad()(fakeRequest)

      status(result) shouldBe Status.OK
    }

    "Return content as HTML" in new Test {

      val result = target.onPageLoad()(fakeRequest)

      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }

  }
}
