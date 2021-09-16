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

package uk.gov.hmrc.ddcopsusermanagmentfrontendpocscala

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

import com.github.tomakehurst.wiremock.client.WireMock._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.Configuration
import uk.gov.hmrc.ddcopsusermanagmentfrontendpocscala.connectors.UserConnector
import uk.gov.hmrc.ddcopsusermanagmentfrontendpocscala.domain.User
import uk.gov.hmrc.http._
import uk.gov.hmrc.http.test.{HttpClientSupport, WireMockSupport}

class UserConnectorSpec
    extends AnyWordSpec
    with WireMockSupport
    with HttpClientSupport
    with Matchers
    with ScalaFutures {
  import scala.concurrent.ExecutionContext.Implicits.global
  implicit val hc: HeaderCarrier = HeaderCarrier()

  val userConnector = new UserConnector(
    httpClient,
    Configuration("connector.url" -> s"$wireMockUrl/api")
  )

  "user connector" should {
    "be able to fetch users" in {
      stubFor(
        get(urlEqualTo("/api?seed=John"))
          .willReturn(
            aResponse()
              .withStatus(200)
              .withBody("""
                  |{
                  |  "results":[
                  |    {
                  |      "name":{
                  |         "title":"mr",
                  |         "first":"John",
                  |         "last":"Lennon"
                  |      }
                  |    }
                  |  ]
                  |}""".stripMargin)
          )
      )

      userConnector.search("John").futureValue shouldBe List(
        User(title = "mr", firstName = "John")
      )

      verify(getRequestedFor(urlEqualTo("/api?seed=John")))
    }
  }
}
