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

import javax.inject.{Inject, Singleton}
import models.errors.{Error, ForbiddenError, ValidationError}
import config.ErrorHandler
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import controllers.actions.TimescalesAction
import services.TimescalesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Logger
import views.html.upload_timescales
import views.html.timescales_upload_complete
import models.UpdateDisplayDetails
import scala.concurrent.Future
import java.nio.file._
import scala.util.{Success, Failure, Try}

@Singleton
class TimescalesController @Inject() (timescalesService: TimescalesService,
                                      timescalesSecuredAction: TimescalesAction,
                                      errorHandler: ErrorHandler,
                                      view: upload_timescales,
                                      uploadCompleteView: timescales_upload_complete,
                                      mcc: MessagesControllerComponents) extends FrontendController(mcc) with I18nSupport {
  val logger: Logger = Logger(getClass)

  def timescales: Action[AnyContent] = timescalesSecuredAction.async{implicit request => uploadPage()}

  def upload: Action[play.api.mvc.MultipartFormData[play.api.libs.Files.TemporaryFile]] =
    timescalesSecuredAction.async(parse.multipartFormData) { implicit request =>
      request.body.file("timescales") match {
        case Some(timescales) if timescales.contentType.fold(false)(_.contains("json")) =>
          readJsonFile(timescales.ref.path) match {
            case Success(json) =>
              timescalesService.submitTimescales(json).flatMap {
                case Right(response) =>
                  Future.successful(Ok(uploadCompleteView(response.count, response.lastUpdate.map(UpdateDisplayDetails(_)))))
                case Left(err) =>
                  logger.error(s"Failed to submit timescales update, err = $err")
                  uploadPage(Some("timescales.error.invalid"))
              }
            case Failure(err) =>
              logger.warn(s"Selected timescale update file is invalid, err = $err")
              uploadPage(Some("timescales.error.invalid"))
          }
        case Some(_) => uploadPage(Some("timescales.error.notjson"))
        case _ => uploadPage(Some("timescales.error.noselection"))
      }
    }

  private def uploadPage(error: Option[String] = None)(implicit request: Request[_]): Future[Result] =
    timescalesService.details().map {
      case Right(response) =>
        val updateDisplayDetails: Option[UpdateDisplayDetails] = response.lastUpdate.map(UpdateDisplayDetails(_))
        error.fold(Ok(view(response.count, updateDisplayDetails, None)))(_ => BadRequest(view(response.count, updateDisplayDetails, error)))
      case Left(ValidationError) =>
        BadRequest(Json.toJson[Error](ValidationError))
      case Left(ForbiddenError) =>
        Unauthorized(Json.toJson[Error](ForbiddenError))
      case Left(error) =>
        logger.error(s"Timescales service failure, err = $error")
        InternalServerError(Json.toJson(error))
    }

  private def readJsonFile(uploadFile: Path): Try[JsValue] =
    Try(Files.newInputStream(uploadFile, StandardOpenOption.READ)).flatMap{f =>
      val result = Try(Json.parse(f))
      f.close()
      result
    }
}
