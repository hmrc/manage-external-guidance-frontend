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

package controllers

import config.ErrorHandler
import controllers.actions.TwoEyeReviewerIdentifierAction
import forms.TwoEyePageReviewFormProvider
import javax.inject.{Inject, Singleton}
import models.PageReviewDetail
import models.PageReviewStatus._
import models.errors.{NotFoundError, StaleDataError}
import models.forms.TwoEyePageReview
import play.api.Logger
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.ReviewService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
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

  val logger: Logger = Logger(getClass)

  def onPageLoad(processId: String, pageUrl: String, index: Int): Action[AnyContent] = twoEyeReviewerIdentifierAction.async { implicit request =>
    reviewService.approval2iPageReview(processId, s"/$pageUrl") map {
      case Right(pageReviewDetail) =>
        val form: Form[TwoEyePageReview] = pageReviewDetail.result.fold(formProvider()) { answer =>
          formProvider().bind(Map("answer" -> answer.toString))
        }
        Ok(view(processId, s"/$pageUrl", pageReviewDetail.pageTitle, form, index))

      case Left(err) =>
        // Handle stale data, internal server and any unexpected errors
        logger.error(s"Request for approval 2i page review for process $processId and pageUrl /$pageUrl returned error $err")
        InternalServerError(errorHandler.internalServerErrorTemplate)
    }
  }

  def onSubmit(processId: String,
               pageUrl: String,
               pageTitle: String,
               index: Int): Action[AnyContent] = twoEyeReviewerIdentifierAction.async { implicit request =>
    formProvider()
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[TwoEyePageReview]) => { Future.successful(BadRequest(view(processId, s"/$pageUrl", pageTitle, formWithErrors, index))) },
        result => {
          val reviewDetail = PageReviewDetail(processId,
                                              s"/$pageUrl",
                                              pageTitle,
                                              Some(result.answer),
                                              Complete,
                                              updateUser = Some(s"${request.credId}:${request.name}"))
          reviewService.approval2iPageReviewComplete(processId, s"/$pageUrl", reviewDetail).map {
            case Right(_) => Redirect(routes.TwoEyeReviewController.approval(processId).withFragment(s"page-link-$index"))
            case Left(NotFoundError) =>
              logger.error(s"Unable to retrieve approval 2i page review for process $processId, url $pageUrl")
              NotFound(errorHandler.notFoundTemplate)
            case Left(StaleDataError) =>
              logger.warn(s"The requested approval 2i review for process $processId, url $pageUrl can no longer be found")
              NotFound(errorHandler.notFoundTemplate)
            case Left(err) =>
              // Handle internal server and any unexpected errors
              logger.error(s"Request for approval 2i review process for process $processId, url $pageUrl returned error $err")
              InternalServerError(errorHandler.internalServerErrorTemplate)
          }
        }
      )
  }
}
