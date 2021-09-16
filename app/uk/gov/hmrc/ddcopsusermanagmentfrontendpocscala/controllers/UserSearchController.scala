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

package uk.gov.hmrc.ddcopsusermanagmentfrontendpocscala.controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.ddcopsusermanagmentfrontendpocscala.connectors.UserConnector
import uk.gov.hmrc.ddcopsusermanagmentfrontendpocscala.views.html.UserSearchPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class UserSearchController @Inject() (
  mcc: MessagesControllerComponents,
  userSearchPage: UserSearchPage,
  userConnector: UserConnector
)(implicit val executionContext: ExecutionContext)
    extends FrontendController(mcc) {

  def userSearch(query: String): Action[AnyContent] = Action.async { implicit request =>
    userConnector
      .search(query, size = 10)
      .map(users =>
        request.headers.get("X-Requested-With") match {
          case Some("XMLHttpRequest") => Ok(Json.toJson(users))
          case _                      => Ok(userSearchPage(users, query))
        }
      )
  }

  // TODO have an autocomplete options endpoint?
}
