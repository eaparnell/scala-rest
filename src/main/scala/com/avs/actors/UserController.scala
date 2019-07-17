package com.avs.actors

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{ActorSystem, OneForOneStrategy, Props, SupervisorStrategy}
import akka.event.Logging
import akka.pattern.ask
import akka.routing.SmallestMailboxPool
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Controller for creating User objects. It defines the actor router and
  * sends requests to it, returning the results
  *
  */

class UserController(system: ActorSystem) {

  private val logger = Logging(system, this.getClass)

  private val workers: Int = 5
  private val router = system.actorOf(SmallestMailboxPool(workers, supervisorStrategy = oneForOne).props(routeeProps = Props[UserActor]))
  private implicit val timeout: Timeout = Timeout(5 seconds)

  def process(id: Int): Future[Any] = {
    logger.info(s"Processing id ${id}")
    val result = router ? id
    result
  }


  private def oneForOne: SupervisorStrategy = {
    OneForOneStrategy(maxNrOfRetries = 5, withinTimeRange = 10 seconds) {
      case e: Exception => {
        logger.error(s"An exception has occurred in strategy: ${e.getMessage}")
        Restart
      }
    }
  }

}
