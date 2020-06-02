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

import javax.inject.{Inject, Singleton}

import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.Logger
import play.api.mvc._

import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import controllers.actions.TwoEyeReviewerIdentifierAction
import forms.TwoEyeReviewResultFormProvider
import models.ApprovalStatus

import views.html.twoeye_review_result

import scala.concurrent.Future

@Singleton
class TwoEyeReviewResultController @Inject() (
    twoEyeReviewerIdentifierAction: TwoEyeReviewerIdentifierAction,
    formProvider: TwoEyeReviewResultFormProvider,
    view: twoeye_review_result,
    mcc: MessagesControllerComponents
) extends FrontendController(mcc)
    with I18nSupport {

  val logger = Logger(getClass)

  def onPageLoad(processId: String): Action[AnyContent] = twoEyeReviewerIdentifierAction.async { implicit request =>
    val form: Form[ApprovalStatus] = formProvider()

    Future.successful(Ok(view(processId, form)))

  }

  def onSubmit(processId: String): Action[AnyContent] = twoEyeReviewerIdentifierAction.async { implicit request =>
    val form: Form[ApprovalStatus] = formProvider()

    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[_]) => { Future.successful(BadRequest(view(processId, formWithErrors))) },
        value => {
          // Do something with value
          Future.successful(Redirect(routes.AdminController.approvalSummaries()))
        }
      )
  }
}
