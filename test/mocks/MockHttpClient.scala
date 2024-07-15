/*
 * Copyright 2024 HM Revenue & Customs
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

// /*
//  * Copyright 2023 HM Revenue & Customs
//  *
//  * Licensed under the Apache License, Version 2.0 (the "License");
//  * you may not use this file except in compliance with the License.
//  * You may obtain a copy of the License at
//  *
//  *     http://www.apache.org/licenses/LICENSE-2.0
//  *
//  * Unless required by applicable law or agreed to in writing, software
//  * distributed under the License is distributed on an "AS IS" BASIS,
//  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  * See the License for the specific language governing permissions and
//  * limitations under the License.
//  */

// package mocks

// import org.scalamock.handlers.{CallHandler, CallHandler2, CallHandler4}
// import scala.reflect.runtime.universe
// import play.api.libs.ws.{BodyWritable, DefaultBodyWritables, WSRequest}
// import org.scalamock.scalatest.MockFactory
// import uk.gov.hmrc.http.client.{RequestBuilder, HttpClientV2}
// import uk.gov.hmrc.http.HeaderCarrier
// import scala.concurrent.{ExecutionContext, Future}
// import uk.gov.hmrc.http.HttpReads
// import java.net.URL

// trait MockHttpClientV2 extends MockFactory {

//   val mockHttpClientV2: HttpClientV2 = mock[HttpClientV2]

//   object MockedHttpClientV2 {

//     def post(url: URL): CallHandler[RequestBuilder] = {
//       (mockHttpClientV2
//         .post(_: URL)(_: HeaderCarrier))
//         .expects(url, *)
//     }

//     def get(url: URL): CallHandler[RequestBuilder] = {
//       (mockHttpClientV2
//         .get(_: URL)(_: HeaderCarrier))
//         .expects(url, *)
//     }
//   }
// }

// trait MockRequestBuilder extends MockFactory with DefaultBodyWritables {

//   val mockRequestBuilder: RequestBuilder = mock[RequestBuilder]

//   object MockRequestBuilder {

//     def execute[A](value: A): CallHandler2[HttpReads[A], ExecutionContext, Future[A]] =
//     (mockRequestBuilder
//       .execute(_: HttpReads[A], _: ExecutionContext))
//       .expects(*, *)
//       .returning(Future.successful(value))

//     import scala.reflect.runtime.universe._
//     // def withBody[JsValue](body: JsValue): CallHandler4[JsValue, BodyWritable[JsValue], universe.TypeTag[JsValue], ExecutionContext, RequestBuilder] =
//     //   (mockRequestBuilder
//     //     .withBody(_: JsValue)(_: BodyWritable[JsValue], _: TypeTag[JsValue], _: ExecutionContext))
//     //     .expects(body, *, *, *)
//     //     .returning(mockRequestBuilder)

//     def withBody[JsValue](body: JsValue): CallHandler4[JsValue, BodyWritable[JsValue], TypeTag[JsValue], ExecutionContext, RequestBuilder] =
//       (mockRequestBuilder
//         .withBody(_: JsValue)(_: BodyWritable[JsValue], _: TypeTag[JsValue], _: ExecutionContext))
//         .expects(body, *, *, *)
//         .returning(mockRequestBuilder)


//   }
// }
