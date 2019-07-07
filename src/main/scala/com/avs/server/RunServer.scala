package com.avs.server


import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.avs.domain.User

import scala.concurrent.{ExecutionContextExecutor, Future}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

object RunServer {

  def main(arg: Array[String]) = {
    println("Begin")
    // set up actor system
    implicit val system = ActorSystem("my-system")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    val route = path("heartbeat") {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "System is up!"))
    } ~ path("user") {
      val user = User(1, "John", "Doe")
      complete(user)
    }

    // start the server and register a shutdownhook

    val log = Logging(system, RunServer.getClass)
    log.info("Starting Server.")
    val bindingFuture: Future[Http.ServerBinding] = Http().bindAndHandle(route, "0.0.0.0", 8080)

    CoordinatedShutdown(system).addTask(
      CoordinatedShutdown.PhaseServiceUnbind, "http_shutdown") { () =>
      log.warning("System shutting down.")
      bindingFuture.flatMap(_.unbind())
    }

  }

}
