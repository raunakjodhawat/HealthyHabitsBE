package com.raunakjodhawat.models

import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

import java.sql.Date
import scala.collection.immutable
import com.raunakjodhawat.models.UtilRegistry.ActionPerformed

final case class Habit(
    habit_id: Int,
    user_id: Int,
    habit_name: String,
    description: String,
    creation_date: Date,
    importance_level: String,
    required_time: String
)

final case class Habits(habits: immutable.Seq[Habit])

object HabitRegistry {
  sealed trait Command

  final case class createHabit(
      habit: Habit,
      replyTo: ActorRef[ActionPerformed]
  ) extends Command

  final case class seeAllHabits(replyTo: ActorRef[Habits]) extends Command
  final case class seeOneHabit(habit_id: Int, replyTo: ActorRef[Option[Habit]])
      extends Command
  final case class deleteOneHabit(
      habit_id: Int,
      replyTo: ActorRef[ActionPerformed]
  ) extends Command

  def apply(): Behavior[Command] = registry(Set.empty)

  private def registry(habit: Set[Habit]): Behavior[Command] =
    Behaviors.receiveMessage {
      case createHabit(newHabit, replyTo) =>
        replyTo ! ActionPerformed(s"${newHabit.habit_name} is created")
        registry(habit + newHabit)
      case seeAllHabits(replyTo) =>
        replyTo ! Habits(habit.toSeq)
        Behaviors.same
      case seeOneHabit(habitID, replyTo) =>
        replyTo ! habit.find(h => h.habit_id == habitID)
        Behaviors.same
      case deleteOneHabit(habitID, replyTo) =>
        replyTo ! ActionPerformed(s"$habitID was deleted")
        registry(habit.filterNot(h => h.habit_id == habitID))
    }
}
