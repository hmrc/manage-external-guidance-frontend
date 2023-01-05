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

package controllers.apis

import config.AppConfig
import javax.inject.{Inject, Singleton}
import models.errors.InvalidProcessError
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.ScratchService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import models.errors.Error
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ScratchController @Inject() (appConfig: AppConfig, scratchService: ScratchService, mcc: MessagesControllerComponents) extends FrontendController(mcc) {

  implicit val config: AppConfig = appConfig

  val corsHeaders: Seq[(String, String)] = Seq(
    "Access-Control-Allow-Origin" -> "*",
    "Access-Control-Allow-Headers" -> "*",
    "Access-Control-Allow-Methods" -> "POST, OPTIONS",
    "Access-Control-Expose-Headers" -> "Location"
  )

  def submitScratchProcess(): Action[JsValue] = Action.async(parse.json) { implicit request: Request[JsValue] =>
    scratchService.submitScratchProcess(request.body).map {
      case Right(submissionResponse) =>
        val location: String = s"/guidance-review/scratch/${submissionResponse.id}"
        Created(Json.toJson(submissionResponse)).withHeaders("location" -> location).withHeaders(corsHeaders: _*)
      case Left(err @ Error(Error.UnprocessableEntity, _, _)) => UnprocessableEntity(Json.toJson(err)).withHeaders(corsHeaders: _*)
      case Left(InvalidProcessError) => BadRequest(Json.toJson[Error](InvalidProcessError)).withHeaders(corsHeaders: _*)
      case Left(error) => InternalServerError(Json.toJson(error)).withHeaders(corsHeaders: _*)
    }
  }

  val scratchProcessOptions: Action[AnyContent] = Action.async { _ =>
    Future.successful(
      Ok("").withHeaders(corsHeaders: _*)
    )
  }

}
