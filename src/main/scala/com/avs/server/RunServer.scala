package com.avs.server


import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.avs.actors.UserController
import com.avs.domain.User

import scala.concurrent.{ExecutionContextExecutor, Future}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

import scala.util.Success

/**
  * Main entry point. RunServer sets up the Actor system, routes, and binds to port 8080.
  * Note in production code, routes would probably be broken into separate modules.
  */
object RunServer {

  def main(arg: Array[String]) = {
    println("Begin")
    // set up actor system
    implicit val system = ActorSystem("my-system")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    val controller = new UserController(system)

    // define routes for server
    // heartbeat for possible use with a load balancer
    val route = path("heartbeat") {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "System is up!"))

      // Retrieve a user object by id
    } ~ path("user" / IntNumber) { userId =>
      val userFuture = controller.process(userId)
      // Add code to execute upon completion of the Future.
      // User object will be automatically converted to JSON via circe library.
      onComplete(userFuture) {
        // return json representation
        case (Success(user: User)) => complete(user)
        case _ => complete(StatusCodes.InternalServerError, "Unknown condition has occurred")
      }
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
