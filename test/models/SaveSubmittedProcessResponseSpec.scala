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

package models

import base.BaseSpec
import play.api.libs.json._

class SaveSubmittedProcessResponseSpec extends BaseSpec {

  val id: String = "test1234"
  val json: JsValue = Json.obj("id" -> id)

  "Serializing a submitted process response into JSON" should {

    "Generate the correct JSON representation for the response" in {

      val response: SaveSubmittedProcessResponse = SaveSubmittedProcessResponse(id)

      val actual: JsValue = Json.toJson(response)

      actual shouldBe json
    }

  }

  "Deserializing JSON into an instance of the class SaveSubmittedProcessResponse" should {

    "Create a correct instance of the class" in {

      val expected: SaveSubmittedProcessResponse = SaveSubmittedProcessResponse(id)

      val actual: SaveSubmittedProcessResponse = json.as[SaveSubmittedProcessResponse]

      actual shouldBe expected
    }

  }

}