package com.raunakjodhawat.routes

import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.raunakjodhawat.models.UserRegistry.{CreateUser, GetAllUsers}
import com.raunakjodhawat.models.UtilRegistry.ActionPerformed
import com.raunakjodhawat.models.{User, UserRegistry, Users}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserRoutes(userRegistry: ActorRef[UserRegistry.Command])(implicit
    val system: ActorSystem[_]
) {
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._

  private implicit val timeout = Timeout.create(
    system.settings.config.getDuration("my-app.routes.ask-timeout")
  )

  def createUser(user: User): Future[ActionPerformed] =
    userRegistry.ask(CreateUser(user, _))

  def getAllUsers(): Future[Users] = userRegistry.ask(GetAllUsers(_))

  val userRoutes: Route = {
    path("user") {
      put {
        entity(as[User]) { user =>
          val operation = createUser(user)
          operation.onComplete(_ => "done")
          complete(200, "all done")
        }
      }
      get { _ =>
        onSuccess(getAllUsers()) { response => complete(Users(response.users)) }
      }
    }
  }
}
