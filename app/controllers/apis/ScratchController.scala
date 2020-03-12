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

package controllers.apis

import config.AppConfig
import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import models.errors.{ExternalGuidanceServiceError, InvalidProcessError}
import services.GuidanceService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class ScratchController @Inject() (appConfig: AppConfig, guidanceService: GuidanceService, mcc: MessagesControllerComponents) extends FrontendController(mcc) {

  implicit val config: AppConfig = appConfig

  def scratchProcess(): Action[JsValue] = Action.async(parse.json) { implicit request: Request[JsValue] =>
    scratchProcess(request.body)

  }

  val scratchProcessOptions: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(
      Ok("").withHeaders(
        "Access-Control-Allow-Origin" -> "*",
        "Access-Control-Allow-Headers" -> "*",
        "Access-Control-Allow-Methods" -> "POST, OPTIONS"
      )
    )
  }

  private def scratchProcess(process: JsValue)(implicit hc: HeaderCarrier): Future[Result] = {

    guidanceService.scratchProcess(process).map {
      case Right(submissionResponse) => {

        val location: String = s"${appConfig.baseUrl}/scratch/${submissionResponse.id}"

        Created(Json.toJson(submissionResponse)).withHeaders("location" -> location)
      }
      case Left(error) => {
        error match {
          case InvalidProcessError => BadRequest(Json.toJson(error))
          case ExternalGuidanceServiceError => InternalServerError(Json.toJson(error))
          case _ => InternalServerError(Json.toJson(error))
        }
      }
    }

  }

}
