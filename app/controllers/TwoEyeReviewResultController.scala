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
import forms.TwoEyeReviewResultFormProvider
import javax.inject.{Inject, Singleton}
import models.ApprovalStatus
import models.audit.{PublishedEvent, TwoEyeReviewCompleteEvent}
import models.errors.{IncompleteDataError, NotFoundError, StaleDataError}
import models.requests.IdentifierRequest
import play.api.Logger
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.{AuditService, ReviewService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.{twoeye_complete, twoeye_confirm_error, twoeye_review_result}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class TwoEyeReviewResultController @Inject() (
    errorHandler: ErrorHandler,
    twoEyeReviewerIdentifierAction: TwoEyeReviewerIdentifierAction,
    formProvider: TwoEyeReviewResultFormProvider,
    view: twoeye_review_result,
    confirmation_view: twoeye_complete,
    errorView: twoeye_confirm_error,
    reviewService: ReviewService,
    auditService: AuditService,
    mcc: MessagesControllerComponents
) extends FrontendController(mcc)
    with I18nSupport {

  val logger: Logger = Logger(getClass)

  def onPageLoad(processId: String): Action[AnyContent] = twoEyeReviewerIdentifierAction.async { implicit request =>

    reviewService.approval2iReviewCheck(processId) map {
      case Right(()) =>
        val form: Form[ApprovalStatus] = formProvider()
        Ok(view(processId, form))
      case Left(IncompleteDataError) => Ok(errorView(processId))
      case Left(NotFoundError) =>
        logger.error(s"Unable to check 2i review approval for process $processId not found")
        NotFound(errorHandler.notFoundTemplate)
      case Left(StaleDataError) =>
        logger.warn(s"The requested 2i review approval check for process $processId failed - stale data")
        NotFound(errorHandler.notFoundTemplate)
      case Left(err) =>
        // Handle internal server and any unexpected errors
        logger.error(s"Request for approval 2i review process for process $processId returned error $err")
        InternalServerError(errorHandler.internalServerErrorTemplate)
    }

  }

  def onSubmit(processId: String): Action[AnyContent] = twoEyeReviewerIdentifierAction.async { implicit request: IdentifierRequest[_] =>
    val form: Form[ApprovalStatus] = formProvider()

    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[_]) => { Future.successful(BadRequest(view(processId, formWithErrors))) },
        status => {
          reviewService.approval2iReviewComplete(processId, request.credId, request.name, status).map {
            case Right(auditInfo) =>
              auditService.audit(TwoEyeReviewCompleteEvent(auditInfo))
              if (status == ApprovalStatus.Published) {
                auditService.audit(PublishedEvent(auditInfo))
              }
              Ok(confirmation_view(status))
            case Left(NotFoundError) =>
              logger.error(s"Unable to retrieve approval 2i review for process $processId")
              NotFound(errorHandler.notFoundTemplate)
            case Left(StaleDataError) =>
              logger.warn(s"The requested approval 2i review for process $processId can no longer be found")
              NotFound(errorHandler.notFoundTemplate)
            case Left(err) =>
              // Handle stale data, internal server and any unexpected errors
              logger.error(s"Request for approval 2i review process for process $processId returned error $err")
              InternalServerError(errorHandler.internalServerErrorTemplate)
          }
        }
      )
  }
}
