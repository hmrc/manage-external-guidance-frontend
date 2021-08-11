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
import controllers.actions.TwoEyeReviewerAction
import javax.inject.{Inject, Singleton}
import models.errors.{DuplicateKeyError, MalformedResponseError, NotFoundError, StaleDataError}
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.ReviewService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.{duplicate_process_code_error, twoeye_content_review}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class TwoEyeReviewController @Inject() (
    errorHandler: ErrorHandler,
    twoEyeReviewerAction: TwoEyeReviewerAction,
    view: twoeye_content_review,
    duplicate_process_code_error: duplicate_process_code_error,
    reviewService: ReviewService,
    mcc: MessagesControllerComponents
) extends FrontendController(mcc)
    with I18nSupport {

  val logger: Logger = Logger(getClass)

  def approval(id: String): Action[AnyContent] = twoEyeReviewerAction.async { implicit request =>
    reviewService.approval2iReview(id).map {
      case Right(approvalProcessReview) =>
        Ok(view(approvalProcessReview))
      case Left(NotFoundError) =>
        logger.error(s"Unable to retrieve approval 2i review for process $id")
        NotFound(errorHandler.notFoundTemplate)
      case Left(StaleDataError) =>
        logger.error(s"The requested approval 2i review for process $id can no longer be found")
        NotFound(errorHandler.notFoundTemplate)
      case Left(DuplicateKeyError) =>
        logger.error(s"Attempt to review a duplicate process code for process $id")
        BadRequest(duplicate_process_code_error())
      case Left(MalformedResponseError) =>
        logger.error(s"A malformed response was returned for the approval 2i process review for process $id")
        InternalServerError(errorHandler.internalServerErrorTemplate)
      case Left(err) =>
        // Handle stale data, internal server and any unexpected errors
        logger.error(s"Request for approval 2i review process for process $id returned error $err")
        InternalServerError(errorHandler.internalServerErrorTemplate)
    }

  }

}
