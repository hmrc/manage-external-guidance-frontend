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
import controllers.actions.DesignerAction
import play.api.mvc._
import services.ProcessAdminService
import views.html.process_admin.{admin_signin, approval_summaries, archived_summaries, published_summaries, active_summaries}
import javax.inject.{Inject, Singleton}
import models.admin.{Page, PublishedList, ApprovalsList, ArchivedList, ActiveList}

@Singleton
class StrideAdminController @Inject() (
    appConfig: AppConfig,
    designerAuthenticatedAction: DesignerAction,
    errorHandler: ErrorHandler,
    publishedView: published_summaries,
    archivedView: archived_summaries,
    approvalsView: approval_summaries,
    activeView: active_summaries,
    signin: admin_signin,
    adminService: ProcessAdminService,
    mcc: MessagesControllerComponents
) extends AbstractProcessAdminController(appConfig, errorHandler, publishedView, archivedView, approvalsView, activeView, adminService, mcc) {

  val PageUrls: Map[Page, String] = Map(
    PublishedList -> s"/external-guidance${controllers.routes.StrideAdminController.listPublished.url}",
    ApprovalsList -> s"/external-guidance${controllers.routes.StrideAdminController.listApprovals.url}",
    ArchivedList -> s"/external-guidance${controllers.routes.StrideAdminController.listArchived.url}",
    ActiveList -> s"/external-guidance${controllers.routes.StrideAdminController.listActive.url}"
  )

  def listPublished: Action[AnyContent] = designerAuthenticatedAction.async { implicit request => published(PageUrls, routes.StrideAdminController.getPublished _) }

  def getPublished(processCode: String): Action[AnyContent] = designerAuthenticatedAction.async { implicit request => getPublishedGuidance(processCode) }

  def listApprovals: Action[AnyContent] = designerAuthenticatedAction.async { implicit request => approvals(PageUrls, routes.StrideAdminController.getApproval _) }

  def getApproval(processCode: String): Action[AnyContent] = designerAuthenticatedAction.async { implicit request => getApprovalGuidance(processCode) }

  def listArchived: Action[AnyContent] = designerAuthenticatedAction.async { implicit request => archived(PageUrls, routes.StrideAdminController.getArchived _) }

  def getArchived(id: String): Action[AnyContent] = designerAuthenticatedAction.async { implicit request => getArchivedGuidance(id) }

  def listActive: Action[AnyContent] = designerAuthenticatedAction.async { implicit request => active(PageUrls, routes.StrideAdminController.getActive _) }

  def getActive(id: String, version: Long, timescalesVersion: Option[Long], ratesVersion: Option[Long]): Action[AnyContent] = designerAuthenticatedAction.async { implicit request => 
    getActiveGuidance(id, version, timescalesVersion, ratesVersion) 
  }

}
