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

package controllers.apis

import config.AppConfig
import javax.inject.{Inject, Singleton}
import models.{ApprovalResponse, RequestOutcome}
import models.errors.{Error, InvalidProcessError}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.ApprovalService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ApprovalController @Inject() (appConfig: AppConfig, approvalService: ApprovalService, mcc: MessagesControllerComponents) extends FrontendController(mcc) {

  implicit val config: AppConfig = appConfig

  val corsHeaders: Seq[(String, String)] = Seq(
    "Access-Control-Allow-Origin" -> "*",
    "Access-Control-Allow-Headers" -> "*",
    "Access-Control-Allow-Methods" -> "POST, OPTIONS"
  )

  def submitFor2iReview(): Action[JsValue] = Action.async(parse.json) { implicit request: Request[JsValue] =>
    checkSubmissionReturn(approvalService.submitFor2iReview(request.body))
  }

  def submitForFactCheck(): Action[JsValue] = Action.async(parse.json) { implicit request: Request[JsValue] =>
    checkSubmissionReturn(approvalService.submitForFactCheck(request.body))
  }

  private def checkSubmissionReturn(result: Future[RequestOutcome[ApprovalResponse]]): Future[Result] = {
    result.map {
      case Right(approvalResponse) => Created(Json.toJson(approvalResponse)).withHeaders(corsHeaders: _*)
      case Left(err @ Error(Error.UnprocessableEntity, _, _)) => UnprocessableEntity(Json.toJson(err)).withHeaders(corsHeaders: _*)
      case Left(InvalidProcessError) => BadRequest(Json.toJson[Error](InvalidProcessError)).withHeaders(corsHeaders: _*)
      case Left(error) => InternalServerError(Json.toJson(error)).withHeaders(corsHeaders: _*)
    }
  }

  val options: Action[AnyContent] = Action.async { _ =>
    Future.successful(
      Ok("").withHeaders(corsHeaders: _*)
    )
  }

}
