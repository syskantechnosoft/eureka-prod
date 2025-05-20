package com.example.eureka.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class EurekaServerSimulation extends Simulation {

  // Common HTTP configuration
  val httpProtocol = http
    .baseUrl("http://localhost:8761") // Replace with your host in test environment
    .acceptHeader("application/json")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Gatling/Performance-Test")

  // Basic authentication headers for Eureka server
  val headers = Map(
    "Authorization" -> "Basic YWRtaW46YWRtaW4=" // admin:admin (Base64 encoded)
  )

  // Health check scenario
  val healthCheckScenario = scenario("Health Check Endpoint")
    .exec(
      http("Health Check Request")
        .get("/actuator/health")
        .headers(headers)
        .check(status.is(200))
    )

  // Eureka applications registry scenario
  val applicationsScenario = scenario("Get Applications Registry")
    .exec(
      http("Get Applications")
        .get("/eureka/apps")
        .headers(headers ++ Map("Accept" -> "application/json"))
        .check(status.is(200))
    )

  // Define load simulation
  setUp(
    healthCheckScenario.inject(
      rampUsers(100).during(30.seconds),
      constantUsersPerSec(10).during(2.minutes)
    ),
    applicationsScenario.inject(
      rampUsers(50).during(30.seconds),
      constantUsersPerSec(5).during(2.minutes)
    )
  ).protocols(httpProtocol)
    .assertions(
      global.responseTime.percentile3.lt(1000), // 75th percentile < 1s
      global.successfulRequests.percent.gt(95)  // At least 95% success rate
    )
}
