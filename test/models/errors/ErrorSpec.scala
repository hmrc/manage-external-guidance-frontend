/*
 * Copyright 2021 HM Revenue & Customs
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

package models.errors

import play.api.libs.json._

import base.BaseSpec

class ErrorSpec extends BaseSpec {

  "Serialising an error object into JSON" should {

    "Generate the correct JSON" in {

      val expected: JsValue = Json.parse(
        """|{
           | "code": "500",
           | "message": "Internal server error"
           |}""".stripMargin
      )

      val error: Error = Error("500", "Internal server error")

      val actual: JsValue = Json.toJson(error)

      actual shouldBe expected
    }
  }

  "Deserialising JSON into an instance of the class Error" should {

    "Create a correct instance of the class Error" in {

      val expected: Error = Error("500", "Internal server error")

      val serializedError: JsValue = Json.parse(
        """|{
           | "code": "500",
           | "message": "Internal server error"
           |}""".stripMargin
      )

      val actual: Error = serializedError.as[Error]

      actual shouldBe expected
    }

  }

}
