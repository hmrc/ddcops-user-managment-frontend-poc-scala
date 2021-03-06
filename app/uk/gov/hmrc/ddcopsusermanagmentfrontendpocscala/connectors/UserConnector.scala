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

package uk.gov.hmrc.ddcopsusermanagmentfrontendpocscala.connectors

import play.api.Configuration
import play.api.libs.functional.syntax._
import play.api.libs.json._
import uk.gov.hmrc.ddcopsusermanagmentfrontendpocscala.domain.User
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserConnector @Inject() (httpClient: HttpClient, configuration: Configuration)(implicit
  executionContext: ExecutionContext
) {
  val baseUrl: String =
    configuration.get[String](
      "connector.url"
    )

  implicit val readsUser: Reads[User] = (
    (for {
      first <- (JsPath \ "name" \ "first").read[String]
      last  <- (JsPath \ "name" \ "last").read[String]
    } yield s"$first $last") and
      (JsPath \ "email").read[String] and
      (JsPath \ "login" \ "username").read[String]
  )(User.apply _)

  def search(query: String, size: Int)(implicit hc: HeaderCarrier): Future[List[User]] =
    httpClient
      .GET[HttpResponse](url"$baseUrl?seed=$query&results=$size")
      .map(response => Json.parse(response.body)("results").as[List[User]])
}
