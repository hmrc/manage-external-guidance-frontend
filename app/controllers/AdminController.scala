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
import javax.inject.{Inject, Singleton}
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.ApprovalService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import play.api.Logger
import views.html.{process_review, approval_summary_list}
import scala.concurrent.ExecutionContext.Implicits.global
import java.time.LocalDate
import models.{ApprovalProcessReview, PageReview, PageReviewStatus}
import PageReviewStatus._
import scala.concurrent.Future

@Singleton
class AdminController @Inject() (
    errorHandler: ErrorHandler,
    view: approval_summary_list,
    previewView : process_review,
    approvalService: ApprovalService,
    mcc: MessagesControllerComponents
) extends FrontendController(mcc)
    with I18nSupport {

  val logger = Logger(getClass)

  def approvalSummaries: Action[AnyContent] = Action.async { implicit request =>
    approvalService.approvalSummaries.map {
      case Right(processList) => Ok(view(processList))
      case Left(err) =>
        logger.warn(s"Unable to retrieve list of approval process summaries, err = $err")
        BadRequest(errorHandler.notFoundTemplate)
    }

  }

  def approval2iReviewInfo(id: String): Action[AnyContent] = Action.async { implicit request =>
    val processReview = ApprovalProcessReview(
        "oct9005",
        "Telling HMRC about extra income",
        LocalDate.of(2020, 5, 10),
        List(PageReview("id1", "how-did-you-earn-extra-income", Reviewed),
          PageReview("id2", "sold-goods-or-services/did-you-only-sell-personal-possessions", NotStarted),
          PageReview("id3", "sold-goods-or-services/have-you-made-a-profit-of-6000-or-more", NotStarted),
          PageReview("id4", "sold-goods-or-services/have-you-made-1000-or-more", NotStarted),
          PageReview("id5", "sold-goods-or-services/you-do-not-need-to-tell-hmrc", NotStarted),
          PageReview("id6", "rent-a-property/do-you-receive-any-income", NotStarted),
          PageReview("id7", "rent-a-property/have-you-rented-out-a-room", NotStarted)))

    Future.successful(Ok(previewView(processReview)))
  }

}
