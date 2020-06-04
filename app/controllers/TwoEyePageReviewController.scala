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

import config.ErrorHandler
import controllers.actions.TwoEyeReviewerIdentifierAction
import forms.TwoEyePageReviewFormProvider
import javax.inject.{Inject, Singleton}
import models.YesNoAnswer
import models.forms.TwoEyePageReview
import views.html.twoeye_page_review
import play.api.Logger
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class TwoEyePageReviewController @Inject() (
    errorHandler: ErrorHandler,
    twoEyeReviewerIdentifierAction: TwoEyeReviewerIdentifierAction,
    formProvider: TwoEyePageReviewFormProvider,
    view: twoeye_page_review,
    mcc: MessagesControllerComponents
) extends FrontendController(mcc)
    with I18nSupport {

  val logger = Logger(getClass)

  def onPageLoad(processId: String, page: String): Action[AnyContent] = twoEyeReviewerIdentifierAction.async { implicit request =>
    val form: Form[TwoEyePageReview] = formProvider()

    Future.successful(Ok(view(processId, page, form)))
  }

  def onSubmit(processId: String, page: String): Action[AnyContent] = twoEyeReviewerIdentifierAction.async { implicit request =>
    val form: Form[TwoEyePageReview] = formProvider()

    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[TwoEyePageReview]) => { Future.successful(BadRequest(view(processId, page, formWithErrors))) },
        result => {

          Future.successful(Ok("Does the page meet the standards : " + result.answer.toString + " Comment : " + result.comment))
        }
      )
  }
}
