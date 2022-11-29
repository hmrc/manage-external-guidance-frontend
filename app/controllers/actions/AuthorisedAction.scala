/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.actions

import play.api.mvc.Results.Redirect
import controllers.{ProcessAdminController, routes}
import play.api.mvc._
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

case class UserRequest[A](request: Request[A], name: String) extends WrappedRequest(request)

trait AuthorisedAction extends ActionBuilder[UserRequest, AnyContent]

class AuthAction @Inject()(bodyParsers: PlayBodyParsers)(implicit val ec: ExecutionContext) extends AuthorisedAction {
  def invokeBlock[A](request: Request[A], block: UserRequest[A] => Future[Result]): Future[Result] =
    request.session.get(ProcessAdminController.userSessionKey) match {
      case Some(name) => block(UserRequest(request, name))
      case None => Future.successful(Redirect(routes.ProcessAdminController.signIn))
    }

  def parser: BodyParser[AnyContent] = bodyParsers.default
  protected def executionContext: ExecutionContext = ec
}