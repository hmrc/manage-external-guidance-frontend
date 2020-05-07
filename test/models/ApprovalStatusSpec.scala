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

import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import play.api.libs.json.{JsError, Json, JsString, JsObject, JsNull}
import models.ApprovalStatus._

class ApprovalStatusSpec extends WordSpec with MustMatchers with OptionValues with Enumerable.Implicits {

  "ApprovalStatus" must {

    "deserialise valid values" in {

      JsString(SubmittedFor2iReview.toString).validate[ApprovalStatus].asOpt.value mustEqual SubmittedFor2iReview

      JsString(SubmittedForFactCheck.toString).validate[ApprovalStatus].asOpt.value mustEqual SubmittedForFactCheck

      JsString(WithDesignerForUpdate.toString).validate[ApprovalStatus].asOpt.value mustEqual WithDesignerForUpdate

      JsString(ApprovedForPublishing.toString).validate[ApprovalStatus].asOpt.value mustEqual ApprovedForPublishing
    }

    "fail to deserialise invalid values" in {

      JsString("invalidValue").validate[ApprovalStatus] mustEqual JsError("error.invalid")

      JsObject(Seq(("val", JsNull))).validate[ApprovalStatus] mustEqual JsError("error.invalid")
    }

     "serialise" in {

       for{
        v <- ApprovalStatus.values
       } yield Json.toJson(v) mustEqual JsString(v.toString)

     }
  }
}
