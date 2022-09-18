package com.raunakjodhawat.routes

import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.raunakjodhawat.models.UserRegistry.{LoginUser, LogoutUser}
import com.raunakjodhawat.models.UtilRegistry.{
  ActionPerformed,
  ActionPerformedWithError,
  ActionPerformedWithSuccess,
  Command
}
import com.raunakjodhawat.models.User

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class UserRoutes(userRegistry: ActorRef[Command])(implicit
    val system: ActorSystem[_]
) {
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._

  private implicit val timeout = Timeout.create(
    system.settings.config.getDuration("my-app.routes.ask-timeout")
  )

  def loginUser(user: User): Future[ActionPerformed] =
    userRegistry.ask(LoginUser(user, _))

  def logoutUser(user: User): Future[ActionPerformed] =
    userRegistry.ask(LogoutUser(user, _))

  val userRoutes: Route = pathPrefix("user") {
    concat(
      path("login") {
        post {
          entity(as[User]) { user =>
            onComplete(loginUser(user)) {
              case Success(value) => {
                value match {
                  case x: ActionPerformedWithSuccess => complete(200, x)
                  case x: ActionPerformedWithError   => complete(400, x)
                }
              }
              case Failure(exception) => complete(400, exception)
              case _                  => complete(400, "unknown error")
            }
          }
        }
      },
      path("logout") {
        post {
          entity(as[User]) { user =>
            onComplete(logoutUser(user)) {
              case Success(value) => {
                value match {
                  case x: ActionPerformedWithSuccess => complete(200, x)
                  case x: ActionPerformedWithError   => complete(400, x)
                }
              }
              case Failure(exception) => complete(400, exception)
              case _                  => complete(400, "unknown error")
            }
          }
        }
      }
    )
  }
}
