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
import models.audit.AuditEvent
import javax.inject.{Inject, Singleton}
import models.errors.InvalidProcessError
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.{ScratchService, AuditService}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ScratchController @Inject() (appConfig: AppConfig, 
                                   scratchService: ScratchService, 
                                   auditService: AuditService, 
                                   mcc: MessagesControllerComponents) extends FrontendController(mcc) {

  implicit val config: AppConfig = appConfig

  def submitScratchProcess(): Action[JsValue] = Action.async(parse.json) { implicit request: Request[JsValue] =>
    scratchService.submitScratchProcess(request.body).map {
      case Right(submissionResponse) =>
        val location: String = s"/guidance/scratch/${submissionResponse.id}"
        auditService.audit("contentCreated", AuditEvent(s"Scratch process created at $location"))
        Created(Json.toJson(submissionResponse)).withHeaders("location" -> location)
      case Left(InvalidProcessError) => BadRequest(Json.toJson(InvalidProcessError))
      case Left(error) => InternalServerError(Json.toJson(error))
    }
  }

  val scratchProcessOptions: Action[AnyContent] = Action.async { _ =>
    Future.successful(
      Ok("").withHeaders(
        "Access-Control-Allow-Origin" -> "*",
        "Access-Control-Allow-Headers" -> "*",
        "Access-Control-Allow-Methods" -> "POST, OPTIONS"
      )
    )
  }

}
