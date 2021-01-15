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
import controllers.actions.FactCheckerIdentifierAction
import javax.inject.{Inject, Singleton}
import models.errors.{DuplicateKeyError, MalformedResponseError, NotFoundError, StaleDataError}
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.ReviewService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.{duplicate_process_code_error, fact_check_content_review}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class FactCheckController @Inject() (
    errorHandler: ErrorHandler,
    factCheckIdentifierAction: FactCheckerIdentifierAction,
    view: fact_check_content_review,
    duplicate_process_code_error: duplicate_process_code_error,
    reviewService: ReviewService,
    mcc: MessagesControllerComponents
) extends FrontendController(mcc)
    with I18nSupport {

  val logger: Logger = Logger(getClass)

  def approval(id: String): Action[AnyContent] = factCheckIdentifierAction.async { implicit request =>
    reviewService.approvalFactCheck(id).map {
      case Right(approvalProcessReview) => Ok(view(approvalProcessReview))
      case Left(NotFoundError) =>
        logger.error(s"Unable to retrieve approval fact check for process $id")
        NotFound(errorHandler.notFoundTemplate)
      case Left(DuplicateKeyError) =>
        logger.warn(s"Duplicate process code found when attempting to fact check process $id")
        BadRequest(duplicate_process_code_error())
      case Left(StaleDataError) =>
        logger.warn(s"The requested approval fact check for process $id can no longer be found")
        NotFound(errorHandler.notFoundTemplate)
      case Left(MalformedResponseError) =>
        logger.error(s"A malformed response was returned for the approval fact check review for process $id")
        InternalServerError(errorHandler.internalServerErrorTemplate)
      case Left(err) =>
        // Handle stale data, internal server and any unexpected errors
        logger.error(s"Request for approval fact check for process $id returned error $err")
        InternalServerError(errorHandler.internalServerErrorTemplate)
    }

  }
}
