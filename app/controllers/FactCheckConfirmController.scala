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
import controllers.actions.FactCheckerIdentifierAction
import javax.inject.{Inject, Singleton}
import models.ApprovalStatus.WithDesignerForUpdate
import models.audit.FactCheckCompleteEvent
import models.errors.{IncompleteDataError, NotFoundError, StaleDataError}
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.{AuditService, ReviewService}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.{fact_check_complete, fact_check_confirm_error}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class FactCheckConfirmController @Inject()(
    errorHandler: ErrorHandler,
    factCheckIdentifierAction: FactCheckerIdentifierAction,
    view: fact_check_complete,
    errorView: fact_check_confirm_error,
    reviewService: ReviewService,
    auditService: AuditService,
    mcc: MessagesControllerComponents
) extends FrontendController(mcc)
    with I18nSupport {

  val logger: Logger = Logger(getClass)

  def onConfirm(processId: String): Action[AnyContent] = factCheckIdentifierAction.async { implicit request =>
    reviewService.approvalFactCheckComplete(processId, request.credId, request.name, WithDesignerForUpdate).map {
      case Right(ap) =>
        auditService.audit(FactCheckCompleteEvent(request.credId, processId, ap.title))
        Ok(view())
      case Left(NotFoundError) =>
        logger.error(s"FactCheck confirmation: Unable to retrieve approval fact check for process $processId")
        NotFound(errorHandler.notFoundTemplate)
      case Left(StaleDataError) =>
        logger.warn(s"The requested fact check for process $processId can no longer be found")
        NotFound(errorHandler.notFoundTemplate)
      case Left(err) =>
        // Handle internal server and any unexpected errors
        logger.error(s"Request for fact check confirm for process $processId returned error $err")
        InternalServerError(errorHandler.internalServerErrorTemplate)
    }
  }

}
