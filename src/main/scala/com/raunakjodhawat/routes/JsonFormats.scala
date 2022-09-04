package com.raunakjodhawat.routes

import com.raunakjodhawat.models.UtilRegistry.ActionPerformed
import com.raunakjodhawat.models.{
  ChangeLog,
  ChangeLogs,
  Habit,
  HabitChange,
  Habits,
  Schedule,
  User,
  Users,
  WorkLog,
  WorkLogChange,
  WorkLogs
}
import spray.json.{DefaultJsonProtocol, JsonFormat, jsonWriter}

import java.awt.Paint
import java.util.Date

object JsonFormats {
  import DefaultJsonProtocol._

  implicit val userJsonFormat = jsonFormat3(User)
  implicit val usersJsonFormat = jsonFormat1(Users)
  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)

  implicit val scheduleJsonFormat = jsonFormat2(Schedule)
  implicit val habitJsonFormat = jsonFormat6(Habit)
  implicit val habitsJsonFormat = jsonFormat1(Habits)

  implicit val habitChangeJsonFormat = jsonFormat2(HabitChange)
  implicit val workLogChangeJsonFormat = jsonFormat2(WorkLogChange)
  implicit val changeLogJsonFormat = jsonFormat6(ChangeLog)
  implicit val changeLogsJsonFormat = jsonFormat1(ChangeLogs)

  implicit val workLogJSONFormat = jsonFormat5(WorkLog)
  implicit val workLogsJSONFormat = jsonFormat1(WorkLogs)
}
