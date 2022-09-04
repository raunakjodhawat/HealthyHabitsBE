package com.raunakjodhawat.routes

import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.raunakjodhawat.models.{Habit, Habits}
import com.raunakjodhawat.models.HabitRegistry.{
  CreateHabit,
  DeleteHabit,
  EditHabit,
  SeeAllHabits
}
import com.raunakjodhawat.models.UtilRegistry.{ActionPerformed, Command}

import scala.concurrent.Future

class HabitRoutes(habitRegistry: ActorRef[Command])(implicit
    val system: ActorSystem[_]
) {
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._

  private implicit val timeout =
    Timeout.create(
      system.settings.config.getDuration("my-app.routes.ask-timeout")
    )

  def createHabitForAUser(userId: Int, habit: Habit): Future[ActionPerformed] =
    habitRegistry.ask(CreateHabit(userId, habit, _))

  def getAllHabitsForAUser(userId: Int): Future[Habits] =
    habitRegistry.ask(SeeAllHabits(userId, _))

  def editAHabitForAUser(
      userId: Int,
      habitId: Int,
      habit: Habit
  ): Future[Option[Habit]] =
    habitRegistry.ask(EditHabit(userId, habitId, habit, _))

  def deleteAHabitForAUser(userId: Int, habitId: Int): Future[ActionPerformed] =
    habitRegistry.ask(DeleteHabit(userId, habitId, _))

  val habitRoutes: Route =
    path("habit") {
      concat(
        get {
          parameter("userId".as[Int]) { userId =>
            {
              val habits = getAllHabitsForAUser(userId)
              complete(200, habits)
            }
          }
        },
        put {
          parameters("userId".as[Int], "habitId".as[Int]) { (userId, habitId) =>
            entity(as[Habit]) { newHabit =>
              {
                val habit = editAHabitForAUser(userId, habitId, newHabit)
                complete(200, habit)
              }
            }
          }
        },
        post {
          parameters("userId".as[Int]) { (userId: Int) =>
            {
              entity(as[Habit]) { habit =>
                {
                  val habits = createHabitForAUser(userId, habit)
                  complete(200, habits)
                }
              }
            }
          }
        },
        delete {
          parameters("userId".as[Int], "habitId".as[Int]) { (userId, habitId) =>
            {
              val habit = deleteAHabitForAUser(userId, habitId)
              complete(200, habit)
            }
          }
        }
      )
    }
}
