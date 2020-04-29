/*
 * Copyright 2019 HM Revenue & Customs
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

package pages

import play.api.http.Status
import play.api.libs.ws.{WSRequest, WSResponse}
import stubs.{AuditStub, AuthStub}
import support.IntegrationSpec

class HelloWorldISpec extends IntegrationSpec {

  "calling the hello world route" should {
    "return an OK response for an authorized user" in {
      AuditStub.audit()
      AuthStub.authorise()
      val request = buildRequest("/hello-world")
      val response: WSResponse = await(request.get())
      response.status shouldBe Status.OK
    }

    "return a redirect response for an unauthorized user" in {
      AuditStub.audit()
      AuthStub.unauthorised()
      val request = buildRequest("/hello-world")
      val response: WSResponse = await(request.get())
      response.status shouldBe Status.SEE_OTHER

      response.header("location") match {
        case Some(redirectUrl) => redirectUrl shouldBe "/external-guidance/unauthorized"
        case None => fail("Redirect location not defined after failed authentication")
      }
    }
  }

  "calling the bye world route" should {
    "return an INTERNAL SERVER ERROR response" in {
      AuditStub.audit()
      val request = buildRequest("/bye-world")
      val response: WSResponse = await(request.get())
      response.status shouldBe Status.INTERNAL_SERVER_ERROR
    }
  }

}
