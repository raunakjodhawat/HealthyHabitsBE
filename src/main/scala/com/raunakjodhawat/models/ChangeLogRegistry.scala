package com.raunakjodhawat.models

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import com.raunakjodhawat.models.UtilRegistry.{ActionPerformed, Command}

import java.util.Date
import scala.collection.immutable

sealed trait changeValues[A] {
  var old_value: Option[A]
  var new_value: A
}
final case class HabitChange(oldValue: Option[Habit], newValue: Habit)
    extends changeValues[Habit] {
  override var old_value: Option[Habit] = oldValue
  override var new_value: Habit = newValue
}

final case class WorkLogChange(oldValue: Option[Int], newValue: Int)
    extends changeValues[Int] {
  override var old_value: Option[Int] = oldValue
  override var new_value: Int = newValue
}

final case class ChangeLog(
    habit_id: Int,
    user_id: Int,
    habit_change: Option[HabitChange],
    work_log_change: Option[WorkLogChange],
    change_type: Int,
    change_date: String
)

final case class ChangeLogs(changeLog: immutable.Seq[ChangeLog])

object ChangeLogRegistry {
  final case class AddNewHabitChangeLog(
      userId: Int,
      habitId: Int,
      newValue: changeValues[Habit],
      oldValue: Option[changeValues[Habit]],
      replyTo: ActorRef[ActionPerformed]
  ) extends Command

  final case class AddNewWorkLogChangeLog(
      userId: Int,
      habitId: Int,
      newValue: changeValues[Int],
      oldValue: Option[changeValues[Int]],
      replyTo: ActorRef[ActionPerformed]
  ) extends Command

  final case class GetAllChangeLogs(
      userId: Int,
      replyTo: ActorRef[ChangeLogs]
  ) extends Command

//  def apply(): Behavior[Command] = registry(Seq.empty)
//
//  private def registry(value: Seq[ChangeLog]): Behavior[Command] =
//    Behaviors.receiveMessage {
//      case GetAllChangeLogs(userId, replyTo) =>
//        replyTo ! ChangeLogs(
//          value.filter(h => h.user_id == userId)
//        )
//        Behaviors.same
//      case AddNewHabitChangeLog(
//            userId,
//            habitId,
//            newValue,
//            oldValue,
//            replyTo
//          ) =>
//        {
//          replyTo ! ActionPerformed("Habit change is recorded")
//          registry(
//            value :+ ChangeLog(
//              habit_id = habitId,
//              user_id = userId,
//              habit_change = HabitChange(newValue, oldValue),
//              work_log_change = None,
//              change_type = 0,
//              change_date = ""
//            )
//          )
//        }
//        habitChange match {
//          case Some(HabitChange(oldHabit, newH)) =>
//            replyTo ! ActionPerformed("Habit change is recorded")
//            oldHabit match {
//              case Some(old) =>
//                registry(
//                  value :+ ChangeLog(
//                    habitId,
//                    userId,
//                    Some(HabitChange(Some(old), newH)),
//                    None,
//                    0,
//                    new Date()
//                  )
//                )
//              case _ =>
//                registry(
//                  value :+ ChangeLog(
//                    habitId,
//                    userId,
//                    Some(HabitChange(None, newH)),
//                    None,
//                    0,
//                    new Date()
//                  )
//                )
//            }
//          case _ =>
//            workLogChange match {
//              case Some(WorkLogChange(old, newW)) =>
//                replyTo ! ActionPerformed("Work log change successful")
//                old match {
//                  case Some(oldW) =>
//                    registry(
//                      value :+ ChangeLog(
//                        habitId,
//                        userId,
//                        None,
//                        Some(WorkLogChange(Some(oldW), newW)),
//                        1,
//                        new Date()
//                      )
//                    )
//                  case _ =>
//                    registry(
//                      value :+ ChangeLog(
//                        habitId,
//                        userId,
//                        None,
//                        Some(WorkLogChange(None, newW)),
//                        1,
//                        new Date()
//                      )
//                    )
//                }
//              case _ =>
//                replyTo ! ActionPerformed("Error in processing the request")
//            }
//        }
//        Behaviors.same
//    }

}
