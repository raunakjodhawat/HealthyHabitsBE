package com.raunakjodhawat.routes

import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route}
import akka.util.Timeout

import com.raunakjodhawat.models.HabitEditRegistry.{
  AddNewHabitEdit,
  GetAllHabitEdits
}
import com.raunakjodhawat.models.{EditHistory, HabitEdits}
import com.raunakjodhawat.models.UtilRegistry.{ActionPerformed, Command}

import scala.concurrent.Future

class HabitChangeRoutes(habitEditRegistry: ActorRef[Command])(implicit
    val system: ActorSystem[_]
) {
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._

  private implicit val timeout = Timeout.create(
    system.settings.config.getDuration("my-app.routes.ask-timeout")
  )

  def getAllHabitEdits(userId: Int): Future[HabitEdits] =
    habitEditRegistry.ask(GetAllHabitEdits(userId, _))

  def saveHabitEdit(
      habitId: Int,
      userId: Int,
      edit: EditHistory
  ): Future[ActionPerformed] = {
    habitEditRegistry.ask(
      AddNewHabitEdit(userId, habitId, edit, _)
    )
  }

  val changeLogRoutes: Route = {
    path("log/habit") {
      concat(
        get {
          parameter("userId".as[Int]) { (userId) =>
            {
              val changes = getAllHabitEdits(userId)
              complete(200, changes)
            }
          }
        },
        post {
          parameters("userId".as[Int], "habitId".as[Int]) { (userId, habitId) =>
            {
              entity(as[EditHistory]) { edit =>
                {
                  val saveResult = saveHabitEdit(habitId, userId, edit)
                  complete(200, "saveResult")
                }
              }
            }
          }
        }
      )
    }
  }
}
