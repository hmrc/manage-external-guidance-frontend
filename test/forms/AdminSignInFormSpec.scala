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

package forms

import base.BaseSpec
import models.AdminSignInDetails
import play.api.data.{Form, FormError}

class AdminSignInFormSpec extends BaseSpec {

  private trait Test {
    val form: Form[AdminSignInDetails] = AdminSignInForm.form
    val name = "name"
    val password = "password"

    val requiredNameFormError = FormError(name, "error.required")
    val requiredPasswordFormError = FormError(password, "error.required")
  }

  "The fact check page review form provider" should {

    "bind a valid response" in new Test {

      val boundForm: Form[AdminSignInDetails] = form.bind(
        Map(
          name -> name,
          password -> password
        )
      )

      boundForm.apply(name).value shouldBe Some(name)
      boundForm.apply(password).value shouldBe Some(password)
    }

    "raise errore when the name and/or password is missing" in new Test {

      val boundForm: Form[AdminSignInDetails] = form.bind(
        Map(
          "blah" -> "blah"
        )
      )

      boundForm.apply(name).errors shouldEqual Seq(requiredNameFormError)
      boundForm.apply(password).errors shouldEqual Seq(requiredPasswordFormError)
    }

  }
}
