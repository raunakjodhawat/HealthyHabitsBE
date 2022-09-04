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

  def createHabitForAUser(habit: Habit): Future[ActionPerformed] =
    habitRegistry.ask(CreateHabit(habit, _))

  def getAllHabitsForAUser(userId: Int): Future[Habits] =
    habitRegistry.ask(SeeAllHabits(userId, _))

  def editAHabitForAUser(userId: Int, habitId: Int): Future[Option[Habit]] =
    habitRegistry.ask(EditHabit(userId, habitId, _))

  def deleteAHabitForAUser(userId: Int, habitId: Int): Future[ActionPerformed] =
    habitRegistry.ask(DeleteHabit(userId, habitId, _))

}
