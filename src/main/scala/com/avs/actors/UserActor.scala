package com.avs.actors


import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import io.getquill.{MysqlJdbcContext, SnakeCase}

import scala.concurrent.{ExecutionContextExecutor, Future}

/**
  * Main worker actor. Uses Quill to access a mysql table called user.
  */

class UserActor extends Actor with ActorLogging {

  case class User(userId: Int, userName: String)

  implicit val executionContext: ExecutionContextExecutor = context.dispatcher

  lazy val ctx = new MysqlJdbcContext(SnakeCase, "db")

  def receive = {

    case id: Int => {
      val result = Future {
        import ctx._
        val q = quote {
          query[User].filter(u => u.userId == lift(id))
        }
        ctx.run(q)
      }
      log.info(s"Actor is ${this.toString}")
      result pipeTo sender
    }
  }

}
