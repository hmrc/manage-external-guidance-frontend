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

package models

import play.api.libs.json._

import base.BaseSpec

class ScratchResponseSpec extends BaseSpec {

  "Serializing a scratch response into JSON" should {

    "Generate the correct JSON representation for the response" in {

      val expected: JsValue = Json.parse(
        """|{
           | "id":"2020Xy"
           |}""".stripMargin
      )

      val response: ScratchResponse = ScratchResponse("2020Xy")

      val actual: JsValue = Json.toJson(response)

      actual shouldBe expected
    }

  }

  "Deserializing JSON into an instance of the class ScratchResponse" should {

    "Create a correct instance of the class" in {

      val expected: ScratchResponse = ScratchResponse("2020Za")

      val serializedResponse: JsValue = Json.parse(
        """|{
           | "id":"2020Za"
           |}""".stripMargin
      )

      val actual: ScratchResponse = serializedResponse.as[ScratchResponse]

      actual shouldBe expected
    }

  }

}
