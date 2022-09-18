package com.raunakjodhawat.models

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import com.raunakjodhawat.models.UtilRegistry.{
  ActionPerformed,
  ActionPerformedWithSuccess,
  Command
}

import scala.collection.immutable

final case class HabitEdit(
    habit_id: Int,
    user_id: Int,
    change: EditHistory,
    change_date: String
)

final case class HabitEdits(habitChangeLogs: immutable.Seq[HabitEdit])

object HabitEditRegistry {
  final case class AddNewHabitEdit(
      userId: Int,
      habitId: Int,
      change: EditHistory,
      replyTo: ActorRef[ActionPerformed]
  ) extends Command

  final case class GetAllHabitEdits(
      userId: Int,
      replyTo: ActorRef[HabitEdits]
  ) extends Command

  def apply(): Behavior[Command] = registry(Seq.empty)

  private def registry(value: Seq[HabitEdit]): Behavior[Command] =
    Behaviors.receiveMessage {
      case AddNewHabitEdit(userId, habitId, change, replyTo) =>
        replyTo ! ActionPerformedWithSuccess("Change log is added")
        registry(
          value :+ HabitEdit(habitId, userId, change, "")
        )
      case GetAllHabitEdits(userId, replyTo) =>
        replyTo ! HabitEdits(value.filter(v => v.user_id == userId))
        Behaviors.same
    }
}
