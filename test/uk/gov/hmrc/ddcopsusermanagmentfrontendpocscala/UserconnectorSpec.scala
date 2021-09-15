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

import uk.gov.hmrc.http._
import com.github.tomakehurst.wiremock.client.WireMock._
import uk.gov.hmrc.http.test.{HttpClientSupport, WireMockSupport}
import play.api.Configuration
import play.api.libs.functional.syntax._
import play.api.libs.json._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future



case class User(title: String, firstName: String)

class UserConnector(httpClient: HttpClient, configuration: Configuration) {
    val baseUrl: String = configuration.get[String]("connector.url") //.getOrElse(throw new IllegalArgumentException("missing connector.url configuration"))

    implicit val hc: HeaderCarrier = HeaderCarrier()

    implicit val readsUser: Reads[User] = (
        (__ \ "name" \ "title").read[String]
        and (__ \ "name" \ "first").read[String]
    )(User.apply _)

    implicit val readsUsers: Reads[List[User]] = {
        (__ \ "results").read[List[User]]
    }

    def search(query: String): Future[List[User]] = {
        httpClient.GET[List[User]](
          url         = url"$baseUrl/search-user"
        )
    }
}

class UserConnetorSpec extends AnyWordSpec with WireMockSupport with HttpClientSupport with Matchers with ScalaFutures{
    "user connector" should {
        val userConnector = new UserConnector(httpClient, Configuration("connector.url" -> wireMockUrl))
        "be able to fetch users" in {
            stubFor(
              get(urlEqualTo("/search-user"))
                          .willReturn(aResponse().withStatus(200).withBody(
            """{
                  "results":[
                     {
                        "name":{
                           "title":"mr",
                           "first":"John",
                           "last":"Lennon"
                        }
                    }
                ]
            }"""))
            )
            userConnector.search("John Lennon").futureValue shouldBe List(
                            User(title="mr", firstName="John")
            )
            verify( getRequestedFor(urlEqualTo("/search-user")))
        }
    }
}
