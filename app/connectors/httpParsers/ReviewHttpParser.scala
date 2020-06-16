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

import models.errors.{InternalServerError, MalformedResponseError}
import models.{ApprovalProcessReview, ApprovalProcessSummary, PageReviewDetail, RequestOutcome}
import play.api.Logger
import play.api.http.Status._
import uk.gov.hmrc.http.HttpReads

object ReviewHttpParser extends HttpParser {

  val logger: Logger = Logger(getClass)

  implicit val getReviewDetailsHttpReads: HttpReads[RequestOutcome[ApprovalProcessReview]] = {
    case (_, _, response) if response.status == OK =>
      response.validateJson[ApprovalProcessReview] match {
        case Some(data) => Right(data)
        case None =>
          logger.error(s"Unable to parse review data response from external-guidance.")
          Left(MalformedResponseError)
      }
    case (_, _, response) => Left(response.checkErrorResponse)
    case _ =>
      logger.error(s"Received service unavailable response from external-guidance. Service could be having issues.")
      Left(InternalServerError)
  }

  implicit val postReviewCompleteHttpReads: HttpReads[RequestOutcome[Unit]] = {
    case (method, url, response) if response.status == NO_CONTENT => Right(())
    case (method, url, response) => Left(response.checkErrorResponse)
    case _ =>
      logger.error(s"Received service unavailable response from external-guidance. Service could be having issues.")
      Left(InternalServerError)
  }

  implicit val getReviewPageDetailsHttpReads: HttpReads[RequestOutcome[PageReviewDetail]] = {
    case (_, _, response) => response.status match {
      case OK =>
        response.validateJson[PageReviewDetail] match {
          case Some(data) => Right(data)
          case None =>
            logger.error(s"Unable to parse review page data response from external-guidance.")
            Left(MalformedResponseError)
        }
      case _ => Left(response.checkErrorResponse)
    }
  }

  implicit val getApprovalSummaryHttpReads: HttpReads[RequestOutcome[ApprovalProcessSummary]] = {
    case (_, _, response) if response.status == OK =>
      response.validateJson[ApprovalProcessSummary] match {
        case Some(result) => Right(result)
        case None => Left(MalformedResponseError)
      }
    case (_, _, response) => Left(response.checkErrorResponse)
    case unknown =>
      Logger.info(s"unexpected $unknown response received when retrieving summary list")
      Left(InternalServerError)
  }

}
