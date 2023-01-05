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

package controllers

import config.ErrorHandler
import services.AdminService
import controllers.actions.AllRolesAction
import forms.UnpublishConfirmationFormProvider
import models.YesNoAnswer.{No, Yes}
import models.forms.UnpublishConfirmation
import play.api.Logger
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.{unpublish_confirmation, unpublished}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ArchiveController @Inject()(
    errorHandler: ErrorHandler,
    allRolesAction: AllRolesAction,
    adminService: AdminService,
    unpublish_confirmation: unpublish_confirmation,
    unpublished: unpublished,
    formProvider: UnpublishConfirmationFormProvider,
    mcc: MessagesControllerComponents
) extends FrontendController(mcc)
    with I18nSupport {

  val logger: Logger = Logger(getClass)

  def unpublish(processId: String): Action[AnyContent] = allRolesAction.async { implicit request =>
    val form: Form[UnpublishConfirmation] = formProvider().bind(Map("value" -> No.toString))

    adminService.getPublished(processId) map {
      case Right(summary) => Ok(unpublish_confirmation(summary.id, summary.processCode, form))
      case Left(_) => BadRequest(errorHandler.badRequestTemplate)
    }
  }

  def archive(processId: String, processName: String): Action[AnyContent] = allRolesAction.async { implicit request =>
    formProvider()
      .bindFromRequest()
      .fold(
        formWithErrors => {
          Future(BadRequest(unpublish_confirmation(processId, processName, formWithErrors)))
        },
        success => success.answer match {
          case Yes => adminService.archive(processId).map {
            case Right(_) => Ok(unpublished(processName))
            case Left(_) => BadRequest(errorHandler.badRequestTemplate)
          }
          case No  => Future(Redirect(routes.AdminController.approvalSummaries))
        }
      )
  }

}
