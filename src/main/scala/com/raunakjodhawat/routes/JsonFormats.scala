package com.raunakjodhawat.routes

import com.raunakjodhawat.models.UtilRegistry.ActionPerformed
import com.raunakjodhawat.models.{
  EditHistory,
  Habit,
  HabitEdit,
  HabitEdits,
  Habits,
  Schedule,
  User,
  Users,
  WorkLog,
  WorkLogs
}
import spray.json.DefaultJsonProtocol

object JsonFormats {
  import DefaultJsonProtocol._

  implicit val userJsonFormat = jsonFormat3(User)
  implicit val usersJsonFormat = jsonFormat1(Users)
  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)

  implicit val scheduleJsonFormat = jsonFormat2(Schedule)
  implicit val habitJsonFormat = jsonFormat6(Habit)
  implicit val habitsJsonFormat = jsonFormat1(Habits)

  implicit val workLogJSONFormat = jsonFormat5(WorkLog)
  implicit val workLogsJSONFormat = jsonFormat1(WorkLogs)

  implicit val changeLogJsonFormat = jsonFormat2(EditHistory)
  implicit val habitChangeLogJsonFormat = jsonFormat4(HabitEdit)
  implicit val habitChangeLogsJsonFormat = jsonFormat1(HabitEdits)

}
