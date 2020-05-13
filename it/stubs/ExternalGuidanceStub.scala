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

package stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.libs.json.JsValue
import support.WireMockMethods

object ExternalGuidanceStub extends WireMockMethods {

  private val saveScratchUri: String = s"/external-guidance/scratch"
  private val saveApprovalUri: String = s"/external-guidance/approval"
  private val approvalSummaryUri: String = s"/external-guidance/approval"

  def saveScratch(status: Int, response: JsValue): StubMapping = {
    when(method = POST, uri = saveScratchUri)
      .thenReturn(status, response)
  }

  def saveApproval(status: Int, response: JsValue): StubMapping = {
    when(method = POST, uri = saveApprovalUri)
      .thenReturn(status, response)
  }

  def approvalSummary(status: Int, response: JsValue): StubMapping = {
    when(method = GET, uri = approvalSummaryUri)
      .thenReturn(status, response)
  }

}
