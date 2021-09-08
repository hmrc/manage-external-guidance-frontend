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

import javax.inject.{Inject, Singleton}
import models.errors.{ForbiddenError, ValidationError}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.TimescalesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.Logger

@Singleton
class TimescalesController @Inject() (timescalesService: TimescalesService, mcc: MessagesControllerComponents) extends FrontendController(mcc) {
  val logger: Logger = Logger(getClass)

  val corsHeaders: Seq[(String, String)] = Seq(
    "Access-Control-Allow-Origin" -> "https://cc-cdio.guidance.prod.dop.corp.hmrc.gov.uk",
    "Access-Control-Allow-Headers" -> "content-type",
    "Access-Control-Allow-Methods" -> "POST, OPTIONS",
    "Access-Control-Allow-Credentials" -> "true",
    "Access-Control-Expose-Headers" -> "*"
  )

  def submitTimescales(): Action[JsValue] = Action.async(parse.json) { implicit request: Request[JsValue] =>
    timescalesService.submitTimescales(request.body).map {
      case Right(()) =>
        NoContent.withHeaders("location" -> "/guidance-review/timescales").withHeaders(corsHeaders: _*)
      case Left(ValidationError) =>
        BadRequest(Json.toJson(ValidationError)).withHeaders(corsHeaders: _*)
      case Left(ForbiddenError) =>
        logger.error(s"ForbiddenError: Received timescale data without the required role")
        BadRequest(Json.toJson(ForbiddenError)).withHeaders(corsHeaders: _*)
      case Left(error) =>
        InternalServerError(Json.toJson(error)).withHeaders(corsHeaders: _*)
    }
  }

  val timescaleOptions: Action[AnyContent] = Action.async { _ =>
    Future.successful(Ok("").withHeaders(corsHeaders: _*))
  }
}
