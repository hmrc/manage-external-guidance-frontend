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

import config.{AppConfig, ErrorHandler}
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.ProcessAdminService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.process_admin.{approval_summaries, archived_summaries, published_summaries, active_summaries}
import java.time.ZonedDateTime
import scala.concurrent.{ExecutionContext, Future}
import models.admin.navigation.AdminPage

abstract class AbstractProcessAdminController (
    appConfig: AppConfig,
    errorHandler: ErrorHandler,
    publishedView: published_summaries,
    archivedView: archived_summaries,
    approvalsView: approval_summaries,
    activeView: active_summaries,
    adminService: ProcessAdminService,
    mcc: MessagesControllerComponents
) extends FrontendController(mcc)
    with I18nSupport {
  implicit val localDateOrdering: Ordering[ZonedDateTime] = Ordering.by(_.toInstant)
  val logger: Logger = Logger(getClass)
  implicit val ec: ExecutionContext = mcc.executionContext
  val pages: List[AdminPage]

  def published(guidanceCall: String => Call)(implicit request: Request[_]): Future[Result] = 
    adminService.publishedSummaries.map {
      case Right(processList) => Ok(publishedView(processList.sortBy(_.actioned).reverse, pages, guidanceCall))
      case Left(err) =>
        logger.error(s"Unable to retrieve list of published process summaries, err = $err")
        InternalServerError(errorHandler.internalServerErrorTemplate)
    }

  def getPublishedGuidance(processCode: String)(implicit request: Request[_]): Future[Result] = 
    adminService.getPublishedByProcessCode(processCode).map {
      case Right(process) => Ok(process)
      case Left(err) =>
        logger.error(s"Unable to retrieve published process by process code, err = $err")
        BadRequest(errorHandler.notFoundTemplate)
    }

  def approvals(guidanceCall: String => Call)(implicit request: Request[_]): Future[Result] = 
    adminService.approvalSummaries.map {
      case Right(processList) => Ok(approvalsView(processList.sortBy(_.actioned).reverse, pages, guidanceCall))
      case Left(err) =>
        logger.error(s"Unable to retrieve list of approval process summaries, err = $err")
        InternalServerError(errorHandler.internalServerErrorTemplate)
    }

  def getApprovalGuidance(processCode: String)(implicit request: Request[_]): Future[Result] = 
    adminService.getApprovalByProcessCode(processCode).map {
      case Right(process) => Ok(process)
      case Left(err) =>
        logger.error(s"Unable to retrieve published process by process code, err = $err")
        BadRequest(errorHandler.notFoundTemplate)
    }

  def archived(guidanceCall: String => Call)(implicit request: Request[_]): Future[Result] = 
    adminService.archivedSummaries.map {
      case Right(processList) => Ok(archivedView(processList.sortBy(_.actioned).reverse, pages, guidanceCall))
      case Left(err) =>
        logger.error(s"Unable to retrieve list of archived process summaries, err = $err")
        InternalServerError(errorHandler.internalServerErrorTemplate)
    }

  def getArchivedGuidance(id: String)(implicit request: Request[_]): Future[Result] = 
    adminService.getArchivedById(id).map {
      case Right(process) => Ok(process)
      case Left(err) =>
        logger.error(s"Unable to retrieve archived process by id, err = $err")
        BadRequest(errorHandler.notFoundTemplate)
    }
  
  def active(guidanceCall: (String, Long, Option[Long], Option[Long]) => Call)(implicit request: Request[_]): Future[Result] = 
    adminService.activeSummaries.map {
      case Right(summaryList) => Ok(activeView(summaryList.sortBy(_.expiryTime).reverse, pages, guidanceCall))
      case Left(err) =>
        logger.error(s"Unable to retrieve list of archived process summaries, err = $err")
        InternalServerError(errorHandler.internalServerErrorTemplate)
    }

  def getActiveGuidance(id: String, version: Long, timescalesVersion: Option[Long] = None, ratesVersion: Option[Long] = None)(implicit request: Request[_]): Future[Result] = 
    adminService.getActive(id, version, timescalesVersion, ratesVersion).map {
      case Right(process) => Ok(process)
      case Left(err) =>
        logger.error(s"Unable to retrieve archived process by id, err = $err")
        BadRequest(errorHandler.notFoundTemplate)
    }

}
