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
import models.errors.{NotFoundError, StaleDataError}
import models.forms.TwoEyePageReview
import models.{PageReviewDetail, PageReviewStatus}
import play.api.Logger
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.ReviewService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.twoeye_page_review

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class TwoEyePageReviewController @Inject() (
    errorHandler: ErrorHandler,
    twoEyeReviewerIdentifierAction: TwoEyeReviewerIdentifierAction,
    formProvider: TwoEyePageReviewFormProvider,
    view: twoeye_page_review,
    reviewService: ReviewService,
    mcc: MessagesControllerComponents
) extends FrontendController(mcc)
    with I18nSupport {

  val logger = Logger(getClass)

  def onPageLoad(processId: String, page: String): Action[AnyContent] = twoEyeReviewerIdentifierAction.async { implicit request =>
    reviewService.approval2iPageReview(processId, page) map {
      case Right(data) if data.result.isDefined =>
        val form: Form[TwoEyePageReview] = formProvider().bind(Map("answer" -> data.result.fold("")(_.toString)))
        Ok(view(processId, page, form))
        
      case Right(_) => Ok(view(processId, page, formProvider()))
      case Left(err) =>
        // Handle stale data, internal server and any unexpected errors
        logger.error(s"Request for approval 2i page review for process $processId and page $page returned error $err")
        InternalServerError(errorHandler.internalServerErrorTemplate)
    }
  }

  def onSubmit(processId: String, page: String): Action[AnyContent] = twoEyeReviewerIdentifierAction.async { implicit request =>
    val form: Form[TwoEyePageReview] = formProvider()
    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[TwoEyePageReview]) => { Future.successful(BadRequest(view(processId, page, formWithErrors))) },
        result => {
          val reviewDetail = PageReviewDetail(processId, page, Some(result.answer), PageReviewStatus.Complete)
          reviewService.approval2iPageReviewComplete(processId, page, reviewDetail).map {
            case Right(_) => Redirect(routes.TwoEyeReviewController.approval(processId))
            case Left(NotFoundError) =>
              logger.error(s"Unable to retrieve approval 2i page review for process $processId")
              NotFound(errorHandler.notFoundTemplate)
            case Left(StaleDataError) =>
              logger.warn(s"The requested approval 2i review for process $processId can no longer be found")
              NotFound(errorHandler.notFoundTemplate)
            case Left(err) =>
              // Handle internal server and any unexpected errors
              logger.error(s"Request for approval 2i review process for process $processId returned error $err")
              InternalServerError(errorHandler.internalServerErrorTemplate)
          }
        }
      )
  }
}
