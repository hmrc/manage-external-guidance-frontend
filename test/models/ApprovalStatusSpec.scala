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

package models

import models.ApprovalStatus._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.OptionValues
import play.api.libs.json._

class ApprovalStatusSpec extends AnyWordSpec with Matchers with OptionValues with Enumerable.Implicits {

  "ApprovalStatus" must {

    "deserialise valid values" in {

      JsString(Submitted.toString).validate[ApprovalStatus].asOpt.value shouldEqual Submitted

      JsString(InProgress.toString).validate[ApprovalStatus].asOpt.value shouldEqual InProgress

      JsString(Complete.toString).validate[ApprovalStatus].asOpt.value shouldEqual Complete

      JsString(Published.toString).validate[ApprovalStatus].asOpt.value shouldEqual Published
    }

    "fail to deserialise invalid values" in {

      JsString("invalidValue").validate[ApprovalStatus] shouldEqual JsError("error.invalid")

      JsObject(Seq(("val", JsNull))).validate[ApprovalStatus] shouldEqual JsError("error.invalid")
    }

    "serialise" in {

      for {
        v <- ApprovalStatus.values
      } yield Json.toJson(v) shouldEqual JsString(v.toString)

    }
  }
}
