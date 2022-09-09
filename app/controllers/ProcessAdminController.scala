/*
 * Copyright 2022 HM Revenue & Customs
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

import java.time.ZonedDateTime
import config.ErrorHandler
import javax.inject.{Inject, Singleton}
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.ProcessAdminService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import play.api.Logger
import controllers.actions.ProcessAdminAction
import views.html.{archived_summaries, published_summaries}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class ProcessAdminController @Inject() (
    identify: ProcessAdminAction,
    errorHandler: ErrorHandler,
    published: published_summaries,
    archived: archived_summaries,
    adminService: ProcessAdminService,
    mcc: MessagesControllerComponents
) extends FrontendController(mcc)
    with I18nSupport {
  implicit val localDateOrdering: Ordering[ZonedDateTime] = Ordering.by(_.toInstant)
  val logger = Logger(getClass)

  def admin: Action[AnyContent] = identify.async { _ =>
    Future.successful(Redirect(routes.ProcessAdminController.listPublished))
  }

  def listPublished: Action[AnyContent] = identify.async { implicit request =>
    adminService.publishedSummaries.map {
      case Right(processList) => Ok(published(processList.sortBy(_.actioned).reverse))
      case Left(err) =>
        logger.error(s"Unable to retrieve list of published process summaries, err = $err")
        InternalServerError(errorHandler.internalServerErrorTemplate)
    }
  }

  def getPublished(processCode: String): Action[AnyContent] = identify.async { implicit request =>
    adminService.getPublishedByProcessCode(processCode).map {
      case Right(process) => Ok(process)
      case Left(err) =>
        logger.error(s"Unable to retrieve list of published process summaries, err = $err")
        BadRequest(errorHandler.notFoundTemplate)
    }
  }

  def listArchived: Action[AnyContent] = identify.async { implicit request =>
    adminService.archivedSummaries.map {
      case Right(processList) => Ok(archived(processList.sortBy(_.actioned).reverse))
      case Left(err) =>
        logger.error(s"Unable to retrieve list of archived process summaries, err = $err")
        InternalServerError(errorHandler.internalServerErrorTemplate)
    }
  }

  def getArchived(id: String): Action[AnyContent] = identify.async { implicit request =>
    adminService.getArchivedById(id).map {
      case Right(process) => Ok(process)
      case Left(err) =>
        logger.error(s"Unable to retrieve archived process, err = $err")
        BadRequest(errorHandler.notFoundTemplate)
    }
  }
}
