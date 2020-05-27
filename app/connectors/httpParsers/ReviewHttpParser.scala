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

package connectors.httpParsers

import models.errors.{Error, InternalServerError, MalformedResponseError, NotFoundError, StaleDataError}
import models.{ApprovalProcessReview, RequestOutcome}
import play.api.Logger
import play.api.http.Status._
import play.api.libs.json.{JsError, JsSuccess}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object ReviewHttpParser extends HttpParser {

  val logger: Logger = Logger(getClass)

  implicit val getReviewDetailsHttpReads: HttpReads[RequestOutcome[ApprovalProcessReview]] = {
    case (_, _, response) if response.status == OK =>
      response.json.validate[ApprovalProcessReview] match {
        case JsSuccess(data, _) => Right(data)
        case JsError(_) =>
          logger.error(s"Unable to parse review data response from external-guidance.")
          Left(MalformedResponseError)
      }
    case (_, _, response) if response.status == NOT_FOUND =>
      checkNotFoundResponse(response)
    case _ =>
      logger.error(s"Received service unavailable response from external-guidance. Service could be having issues.")
      Left(InternalServerError)
  }

  implicit val postReviewCompleteHttpReads: HttpReads[RequestOutcome[Boolean]] = {
    case (_, _, response) if response.status == NO_CONTENT => Right(true)
    case (_, _, response) if response.status == NOT_FOUND =>
      checkNotFoundResponse(response)
    case _ =>
      logger.error(s"Received service unavailable response from external-guidance. Service could be having issues.")
      Left(InternalServerError)
  }

  private def checkNotFoundResponse(response: HttpResponse) = {
    response.json.validate[Error] match {
      case JsSuccess(error, _) =>
        if (error.code == NotFoundError.code) {
          Left(NotFoundError)
        } else {
          Left(StaleDataError)
        }
      case JsError(_) =>
        logger.error(s"Unable to parse NOT_FOUND response from external-guidance.")
        Left(MalformedResponseError)
    }
  }
}
