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

import scala.concurrent.ExecutionContext.Implicits.global

import play.api.mvc._
import play.api.i18n.I18nSupport
import play.api.Logger

import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import config.ErrorHandler
import models.errors.{MalformedResponseError, NotFoundError}
import services.ReviewService
import views.html.twoeye_content_review

@Singleton
class TwoEyeReviewController @Inject() (
    errorHandler: ErrorHandler,
    view: twoeye_content_review,
    reviewService: ReviewService,
    mcc: MessagesControllerComponents
) extends FrontendController(mcc)
    with I18nSupport {

  val logger = Logger(getClass)

  def approval(id: String): Action[AnyContent] = Action.async { implicit request =>
    reviewService.approval2iReview(id).map {
      case Right(approvalProcessReview) => Ok(view(approvalProcessReview))
      case Left(NotFoundError) => {
        logger.error(s"Unable to retrieve approval 2i process review for process $id")
        NotFound(errorHandler.notFoundTemplate)
      }
      case Left(MalformedResponseError) => {
        logger.error(s"A malformed response was returned for the approval 2i process review for process $id")
        InternalServerError(errorHandler.internalServerErrorTemplate)
      }
      case Left(err) =>
        // Handle stale data, internal server and any unexpected errors
        logger.error(s"Request for approval 2i review process for process $id returned error $err")
        InternalServerError(errorHandler.internalServerErrorTemplate)
    }

  }

}
