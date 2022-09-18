package com.raunakjodhawat.routes

import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.raunakjodhawat.models.UserRegistry.{
  CreateUser,
  GetAUser,
  UpdateAUser
}
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

  def createUser(user: User): Future[ActionPerformed] =
    userRegistry.ask(CreateUser(user, _))

  def getAUser(user_id: Int): Future[Option[User]] =
    userRegistry.ask(GetAUser(user_id, _))

  def updateAUser(user: User): Future[ActionPerformed] =
    userRegistry.ask(UpdateAUser(user, _))

  val userRoutes: Route = pathPrefix("user") {
    concat(
      path("create") {
        put {
          entity(as[User]) { user =>
            onComplete(createUser(user)) {
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
      path("update") {
        put {
          entity(as[User]) { user =>
            onComplete(updateAUser(user)) {
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
      get {
        parameter("user_id".as[Int]) { user_id =>
          {
            onComplete(getAUser(user_id)) {
              case Success(value) =>
                value match {
                  case Some(u) => complete(200, u)
                  case _       => complete(404, s"${user_id} not found")
                }
              case Failure(exception) => complete(400, exception)
            }
          }
        }
      }
    )
  }
}
