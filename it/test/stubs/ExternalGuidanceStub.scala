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

package stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.libs.json.JsValue
import support.WireMockMethods

object ExternalGuidanceStub extends WireMockMethods {

  private val timescalesUri: String = "/external-guidance/timescales"
  private val saveScratchUri: String = s"/external-guidance/scratch"
  private val saveFor2iReviewUri: String = s"/external-guidance/approval/2i-review"
  private val saveForFactCheckUri: String = s"/external-guidance/approval/fact-check"
  private val approvalSummaryUri: String = s"/external-guidance/approval"
  private val approval2iReviewConfirmCheckUri: String = "/external-guidance/approval/oct90005/2i-review/confirm"
  private val approval2iReviewUri: String = "/external-guidance/approval/oct90005/2i-review"
  private val approval2iPageReviewUri: String = "/external-guidance/approval/oct90005/2i-page-review/pageUrl"
  private val factCheckReviewUri: String = "/external-guidance/approval/oct90005/fact-check"
  private val factCheckPageReviewUri: String = "/external-guidance/approval/oct90005/fact-check-page-review/pageUrl"

  def timescales(status: Int, response: JsValue): StubMapping = {
    when(method = GET, uri = timescalesUri)
      .thenReturn(status, response)
  }

  def saveTimescales(status: Int, response: JsValue): StubMapping = {
    when(method = POST, uri = timescalesUri)
      .thenReturn(status, response)
  }

  def saveScratch(status: Int, response: JsValue): StubMapping = {
    when(method = POST, uri = saveScratchUri)
      .thenReturn(status, response)
  }

  def save2iReview(status: Int, response: JsValue): StubMapping = {
    when(method = POST, uri = saveFor2iReviewUri)
      .thenReturn(status, response)
  }

  def saveFactCheck(status: Int, response: JsValue): StubMapping = {
    when(method = POST, uri = saveForFactCheckUri)
      .thenReturn(status, response)
  }

  def approvalSummary(status: Int, response: JsValue): StubMapping = {
    when(method = GET, uri = approvalSummaryUri)
      .thenReturn(status, response)
  }

  def approval2iReview(status: Int, response: JsValue): StubMapping = {
    when(method = GET, uri = approval2iReviewUri)
      .thenReturn(status, response)
  }

  def approval2iReviewCheck(status: Int, response: JsValue): StubMapping = {
    when(method = GET, uri = approval2iReviewConfirmCheckUri)
      .thenReturn(status, response)
  }

  def approval2iReviewComplete(status: Int, response: JsValue): StubMapping = {
    when(method = POST, uri = approval2iReviewUri)
      .thenReturn(status, response)
  }

  def approval2iPageReview(status: Int, response: JsValue): StubMapping = {
    when(method = GET, uri = approval2iPageReviewUri)
      .thenReturn(status, response)
  }

  def approval2iPageReviewComplete(status: Int, response: JsValue): StubMapping = {
    when(method = POST, uri = approval2iPageReviewUri)
      .thenReturn(status, response)
  }

  def factCheck(status: Int, response: JsValue): StubMapping = {
    when(method = GET, uri = factCheckReviewUri)
      .thenReturn(status, response)
  }

  def factCheckComplete(status: Int, response: JsValue): StubMapping = {
    when(method = POST, uri = factCheckReviewUri)
      .thenReturn(status, response)
  }

  def factCheckPageReview(status: Int, response: JsValue): StubMapping = {
    when(method = GET, uri = factCheckPageReviewUri)
      .thenReturn(status, response)
  }

  def factCheckPageReviewComplete(status: Int, response: JsValue): StubMapping = {
    when(method = POST, uri = factCheckPageReviewUri)
      .thenReturn(status, response)
  }
}
