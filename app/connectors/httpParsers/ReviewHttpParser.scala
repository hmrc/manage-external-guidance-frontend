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

import models.ApprovalProcessReview
import models.errors.{Error, NotFoundError}
import play.api.Logger
import play.api.http.Status._
import play.api.libs.json.{JsError, JsSuccess}
import uk.gov.hmrc.http.HttpReads

object ReviewHttpParser extends HttpParser {

  val logger: Logger = Logger(getClass)

  type GetReviewDetailsResponse = Either[GetReviewDetailsFailure, GetReviewDetailsSuccess]

  implicit val getReviewDetailsHttpReads: HttpReads[GetReviewDetailsResponse] = {
    case (_, _, response)
      if response.status == OK =>
        response.json.validate[ApprovalProcessReview] match {
          case JsSuccess(data, _) => Right(ReviewDetailsSuccess(data))
          case JsError(_) => Left(ReviewDetailsMalformed)
        }
    case (_, _, response)
      if response.status == NOT_FOUND =>
        response.json.validate[Error] match {
          case JsSuccess(error, _) =>
            if(error.code == NotFoundError.code) {
              Left(ReviewDetailsNotFound)
            } else {
              Left(ReviewDetailsStale)
            }
          case JsError(_) => Left(ReviewDetailsMalformed)
        }
    case _ =>
      logger.error(s"Received service unavailable response from external-guidance. Service could be having issues.")
      Left(ReviewDetailsFailure(INTERNAL_SERVER_ERROR))
  }

  sealed trait GetReviewDetailsSuccess

  case class ReviewDetailsSuccess(reviewInfo: ApprovalProcessReview) extends GetReviewDetailsSuccess

  sealed trait GetReviewDetailsFailure

  case object ReviewDetailsNotFound extends GetReviewDetailsFailure

  case object ReviewDetailsStale extends GetReviewDetailsFailure

  case object ReviewDetailsMalformed extends GetReviewDetailsFailure

  case class ReviewDetailsFailure(status: Int) extends GetReviewDetailsFailure


}
