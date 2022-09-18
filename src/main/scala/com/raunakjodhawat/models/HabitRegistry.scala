package com.raunakjodhawat.models

import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

import scala.collection.immutable
import com.raunakjodhawat.models.UtilRegistry.{
  ActionPerformed,
  ActionPerformedWithSuccess,
  Command
}

final case class Schedule(
    days_of_week: Array[Int],
    minutes: Int
)

final case class Habit(
    habit_id: Int,
    user_id: Int,
    habit_name: String,
    aspiration: String,
    importance: Int,
    schedule: Schedule
)

final case class Habits(habits: immutable.Seq[Habit])

object HabitRegistry {
  // Create a habit
  final case class CreateHabit(
      userId: Int,
      habit: Habit,
      replyTo: ActorRef[ActionPerformed]
  ) extends Command

  // See all habits for a user
  final case class SeeAllHabits(userId: Int, replyTo: ActorRef[Habits])
      extends Command

  // edit a habit
  final case class EditHabit(
      user_id: Int,
      habit_id: Int,
      habit: Habit,
      replyTo: ActorRef[Option[Habit]]
  ) extends Command

  // delete a habit
  final case class DeleteHabit(
      user_id: Int,
      habit_id: Int,
      replyTo: ActorRef[ActionPerformed]
  ) extends Command

  def apply(): Behavior[Command] = registry(Seq.empty)

  private def registry(habit: Seq[Habit]): Behavior[Command] =
    Behaviors.receiveMessage {
      case CreateHabit(userId, newHabit, replyTo) =>
        replyTo ! ActionPerformedWithSuccess(
          s"${newHabit.habit_name} is created"
        )
        registry(habit.filter(h => h.user_id == userId) :+ newHabit)
      case SeeAllHabits(userId: Int, replyTo) =>
        replyTo ! Habits(habit.filter(h => h.user_id == userId))
        Behaviors.same
      case EditHabit(userId, habitId, editedHabit, replyTo) =>
        replyTo ! Some(editedHabit)
        registry(
          habit.filterNot(h =>
            h.habit_id == habitId && h.user_id == userId
          ) :+ editedHabit
        )
      case DeleteHabit(userId, habitId, replyTo) =>
        replyTo ! ActionPerformedWithSuccess(s"$habitId was deleted")
        registry(
          habit.filterNot(h => h.habit_id == habitId && h.user_id == userId)
        )
    }
}
