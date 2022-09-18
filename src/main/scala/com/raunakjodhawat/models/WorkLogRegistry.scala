package com.raunakjodhawat.models

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.raunakjodhawat.models.UtilRegistry.{
  ActionPerformed,
  ActionPerformedWithSuccess,
  Command
}

import scala.collection.immutable

final case class WorkLog(
    user_id: Int,
    habit_id: Int,
    time_spent: Int,
    time_required: Int,
    record_date: String
)

final case class WorkLogs(workLog: immutable.Seq[WorkLog])

object WorkLogRegistry {
  final case class AddWorkLog(
      workLog: WorkLog,
      replyTo: ActorRef[ActionPerformed]
  ) extends Command

  final case class EditWorkLog(workLog: WorkLog, replyTo: ActorRef[WorkLog])
      extends Command
  final case class GetWorkLog(user_id: Int, replyTo: ActorRef[WorkLogs])
      extends Command

  final case class DeleteWorkLog(
      workLog: WorkLog,
      replyTo: ActorRef[ActionPerformed]
  ) extends Command

  def apply(): Behavior[Command] = registry(Seq.empty)

  private def registry(workLogs: Seq[WorkLog]): Behavior[Command] =
    Behaviors.receiveMessage {
      case AddWorkLog(workLog, replyTo) =>
        replyTo ! ActionPerformedWithSuccess("work Log added")
        registry(workLogs :+ workLog)
      case EditWorkLog(workLog, replyTo) =>
        replyTo ! workLog
        registry(
          workLogs.filterNot(h =>
            h.user_id == workLog.user_id && h.habit_id == workLog.habit_id
          ) :+ workLog
        )
      case GetWorkLog(userId, replyTo) =>
        replyTo ! WorkLogs(workLogs.filter(h => h.user_id == userId))
        Behaviors.same

      case DeleteWorkLog(workLog, replyTo) =>
        replyTo ! ActionPerformedWithSuccess("Work log is deleted")
        registry(
          workLogs.filterNot(h =>
            h.user_id == workLog.user_id && h.habit_id == workLog.habit_id
          )
        )
    }
}
