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

package controllers

import base.ControllerBaseSpec
import java.time.ZonedDateTime
import controllers.actions.FakeTimescalesAction
import mocks.MockTimescalesService
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.mvc._
import play.api.libs.Files.TemporaryFile
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import views.html.upload_timescales
import views.html.timescales_upload_complete
import models.{TimescalesDetail, UpdateDetails}
import play.api.libs.json._
import config.ErrorHandler
import scala.concurrent.Future
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.Files
import java.nio.file.{Files => NioFiles}
import java.nio.file.StandardOpenOption
import models.errors.{NotFoundError, BadRequestError, ValidationError}
import models.errors.ForbiddenError

class TimescaleControllerSpec extends ControllerBaseSpec with GuiceOneAppPerSuite with MockTimescalesService {

  private trait Test {

    implicit val hc: HeaderCarrier = HeaderCarrier()

    val errorHandler: ErrorHandler = injector.instanceOf[ErrorHandler]

    def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]
    implicit val messages: Messages = messagesApi.preferred(FakeRequest("GET", "/"))
    val view = injector.instanceOf[upload_timescales]
    val completeView = injector.instanceOf[timescales_upload_complete]
    val timscalesJsonString = """{"First": 1, "Second": 2, "Third": 3}"""
    val timescalesJson: JsValue = Json.parse(timscalesJsonString)
    val controller = new TimescalesController(mockTimescalesService, FakeTimescalesAction, errorHandler, view, completeView, messagesControllerComponents)

    val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/external-guidance/timescales")
    val lastUpdateTime: ZonedDateTime = ZonedDateTime.of(2020, 1, 1, 12, 0, 1, 0, ZonedDateTime.now.getZone)

    val timescales: Map[String, Int] = Map("First" -> 1, "Second" -> 2, "Third" -> 3)
    val credId: String = "234324234"
    val user: String = "User Blah"
    val email: String = "user@blah.com"
    val updateDetail = UpdateDetails(lastUpdateTime, "234324234", "User Blah", "user@blah.com")
    val timescaleDetails = TimescalesDetail(timescales.size, Some(updateDetail))

    def createMultipartFormData(fileContent: String, contentType: String): MultipartFormData[TemporaryFile] = {
      val (tempFile, fileLength) = createTempJsonFile(fileContent)
      val filePart = new MultipartFormData.FilePart[TemporaryFile](
        key = "timescales",
        filename = "timescales",
        contentType = Some(contentType),
        tempFile,
        fileSize = fileLength
      )

      new MultipartFormData[TemporaryFile](
        dataParts = Map(
          "x-amz-algorithm"         -> Seq("AWS4-HMAC-SHA256"),
          "x-amz-credential"        -> Seq("some-credentials"),
          "x-amz-date"              -> Seq("20180517T113023Z"),
          "policy"                  -> Seq(),
          "x-amz-signature"         -> Seq("some-signature"),
          "acl"                     -> Seq("private"),
          "key"                     -> Seq("file-key"),
          "x-amz-meta-callback-url" -> Seq("http://mylocalservice.com/callback")
        ),
        files    = Seq(filePart),
        badParts = Nil
      )
    }

    def createTempJsonFile(content: String): (TemporaryFile, Int) = {
      val tempFile = Files.SingletonTemporaryFileCreator.create("file", "tmp")
      val writer = NioFiles.newBufferedWriter(tempFile.path, StandardOpenOption.CREATE)
      writer.write(content, 0, content.length)
      writer.close
      (tempFile, content.length)
    }

  }

  "GET /external-guidance/timescales" should {

    "return OK and last update details" in new Test {

      MockTimescalesService.details.returns(Future.successful(Right(timescaleDetails)))

      val result = controller.timescales()(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return BadRequest if receipt from server generates a validation error" in new Test {

      MockTimescalesService.details.returns(Future.successful(Left(ValidationError)))

      val result = controller.timescales()(fakeRequest)
      status(result) shouldBe Status.BAD_REQUEST
    }

    "return Unauthorized if receipt from server generates a forbidden error" in new Test {

      MockTimescalesService.details.returns(Future.successful(Left(ForbiddenError)))

      val result = controller.timescales()(fakeRequest)
      status(result) shouldBe Status.UNAUTHORIZED
    }

    "return Internal error if receipt from server generates a NotFound error" in new Test {

      MockTimescalesService.details.returns(Future.successful(Left(NotFoundError)))

      val result = controller.timescales()(fakeRequest)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
    }
  }

  "POST /external-guidance/timescales" should {
    "Upload valid timescale defns return OK and last update details" in new Test {
      val fakeUploadRequest = FakeRequest("POST", "/external-guidance/timescales").withBody(createMultipartFormData(timscalesJsonString, "application/json"))

      MockTimescalesService.submitTimescales(timescalesJson).returns(Future.successful(Right(timescaleDetails)))
      val result = controller.upload()(fakeUploadRequest)

      status(result) shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }


    "Upload valid timescale defns but submission fails, return BAD_REQUEST and last update details" in new Test {
      val fakeUploadRequest = FakeRequest("POST", "/external-guidance/timescales").withBody(createMultipartFormData(timscalesJsonString, "application/json"))

      MockTimescalesService.submitTimescales(timescalesJson).returns(Future.successful(Left(BadRequestError)))
      MockTimescalesService.details.returns(Future.successful(Right(timescaleDetails)))
      val result = controller.upload()(fakeUploadRequest)

      status(result) shouldBe Status.BAD_REQUEST
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }

    "Upload invalid timescale defns return BadRequest and last update details" in new Test {
      val fakeUploadRequest = FakeRequest("POST", "/external-guidance/timescales").withBody(createMultipartFormData("some text", "application/json"))

      MockTimescalesService.details.returns(Future.successful(Right(timescaleDetails)))
      val result = controller.upload()(fakeUploadRequest)

      status(result) shouldBe Status.BAD_REQUEST
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }

    "Upload valid timescale defns with non json content-type should return BadRequest and last update details" in new Test {
      val fakeUploadRequest = FakeRequest("POST", "/external-guidance/timescales").withBody(createMultipartFormData(timscalesJsonString, "application/txt"))

      MockTimescalesService.details.returns(Future.successful(Right(timescaleDetails)))
      val result = controller.upload()(fakeUploadRequest)

      status(result) shouldBe Status.BAD_REQUEST
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }
  }
}
