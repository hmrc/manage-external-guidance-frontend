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
import controllers.actions.AuthorisedAction
import forms.AdminSignInForm
import models.AdminSignInDetails
import play.api.mvc._
import services.ProcessAdminService
import views.html.process_admin.{admin_signin, approval_summaries, archived_summaries, published_summaries, active_summaries}
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import models.admin.{Page, PublishedList, ApprovalsList, ArchivedList, ActiveList}

object ProcessAdminController {
  val userSessionKey = "userName"
}

@Singleton
class ProcessAdminController @Inject() (
    appConfig: AppConfig,
    authAction: AuthorisedAction,
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
    PublishedList -> s"/external-guidance${controllers.routes.ProcessAdminController.listPublished.url}",
    ApprovalsList -> s"/external-guidance${controllers.routes.ProcessAdminController.listApprovals.url}",
    ArchivedList -> s"/external-guidance${controllers.routes.ProcessAdminController.listArchived.url}",
    ActiveList -> s"/external-guidance${controllers.routes.ProcessAdminController.listActive.url}",
  )

  def signIn: Action[AnyContent] = Action { implicit request =>
    Ok(signin(AdminSignInForm.form))
  }

  def submitSignIn: Action[AnyContent] = Action.async { implicit request =>
    AdminSignInForm.form
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(signin(formWithErrors))),
        form =>
          AdminSignInDetails(form.name, form.password)
            .validate(appConfig)
            .fold({
              val formWithError = AdminSignInForm.form
                                    .fill(AdminSignInDetails("",""))
                                    .withGlobalError(request.messages("admin.signin-error"))
              Future.successful(Unauthorized(signin(formWithError)(request, request.messages)))
            }){ user =>
              Future.successful(Redirect(routes.ProcessAdminController.listPublished.url).addingToSession(ProcessAdminController.userSessionKey -> user))
            }
      )
  }

  def signOut: Action[AnyContent] = authAction.async { implicit request => 
    Future.successful(Redirect(routes.ProcessAdminController.listPublished.url).removingFromSession(ProcessAdminController.userSessionKey))
  }

  def listPublished: Action[AnyContent] = authAction.async { implicit request => published(PageUrls, routes.ProcessAdminController.getPublished _) }

  def getPublished(processCode: String): Action[AnyContent] = authAction.async { implicit request => getPublishedGuidance(processCode) }

  def listApprovals: Action[AnyContent] = authAction.async { implicit request => approvals(PageUrls, routes.ProcessAdminController.getApproval _) }

  def getApproval(processCode: String): Action[AnyContent] = authAction.async { implicit request => getApprovalGuidance(processCode) }

  def listArchived: Action[AnyContent] = authAction.async { implicit request => archived(PageUrls, routes.ProcessAdminController.getArchived _) }

  def getArchived(id: String): Action[AnyContent] = authAction.async { implicit request => getArchivedGuidance(id) }

  def listActive: Action[AnyContent] = authAction.async { implicit request => active(PageUrls, routes.ProcessAdminController.getActive _) }

  def getActive(id: String, version: Long, timescalesVersion: Option[Long], ratesVersion: Option[Long]): Action[AnyContent] = authAction.async { implicit request => 
    getActiveGuidance(id, version, timescalesVersion, ratesVersion) 
  }
}
