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

package models

import java.time.LocalDate

import models.PageReviewStatus.NotStarted

trait ReviewData {

  val id: String = "Oct20001"

  val reviewInfo: ApprovalProcessReview = ApprovalProcessReview(
    id,
    "Telling HMRC about extra income",
    LocalDate.of(2020, 5, 10),
    List(
      PageReview("id1", "how-did-you-earn-extra-income", NotStarted),
      PageReview("id2", "sold-goods-or-services/did-you-only-sell-personal-possessions", NotStarted),
      PageReview("id3", "sold-goods-or-services/have-you-made-a-profit-of-6000-or-more", NotStarted),
      PageReview("id4", "sold-goods-or-services/have-you-made-1000-or-more", NotStarted),
      PageReview("id5", "sold-goods-or-services/you-do-not-need-to-tell-hmrc", NotStarted),
      PageReview("id6", "rent-a-property/do-you-receive-any-income", NotStarted),
      PageReview("id7", "rent-a-property/have-you-rented-out-a-room", NotStarted)
    )
  )

}
