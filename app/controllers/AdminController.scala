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

import java.time.LocalDate
import config.ErrorHandler
import javax.inject.{Inject, Singleton}
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.ApprovalService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import play.api.Logger
import controllers.actions.AllRolesAction
import views.html.approval_summary_list
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class AdminController @Inject() (
    identify: AllRolesAction,
    errorHandler: ErrorHandler,
    view: approval_summary_list,
    approvalService: ApprovalService,
    mcc: MessagesControllerComponents
) extends FrontendController(mcc)
    with I18nSupport {
  implicit val localDateOrdering: Ordering[LocalDate] = Ordering.by(_.toEpochDay)
  val logger = Logger(getClass)

  def approvalSummaries: Action[AnyContent] = identify.async { implicit request =>
    approvalService.approvalSummaries.map {
      case Right(processList) => Ok(view(processList.sortBy(_.lastUpdated).reverse))
      case Left(err) =>
        logger.error(s"Unable to retrieve list of approval process summaries, err = $err")
        BadRequest(errorHandler.notFoundTemplate)
    }

  }

}
