package com.avs.actors


import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import com.avs.domain.User

import scala.concurrent.{ExecutionContextExecutor, Future}

/**
  * Main worker actor. Creates a dummy Future simulating what would happen if a database call was issues.
  * Thread.sleep simulates a load so that multiple actors will be scheduled at once.
  */
class UserActor extends Actor with ActorLogging {
  implicit val executionContext: ExecutionContextExecutor = context.dispatcher

  def receive = {

    case id: Int => {
      val result = Future {
        User(id, "John", "Doe")
      }
      log.info(s"Actor is ${this.toString}")
      Thread.sleep(3000)
      result pipeTo sender
    }
  }

}
