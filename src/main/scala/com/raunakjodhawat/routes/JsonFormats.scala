package com.raunakjodhawat.routes

import com.raunakjodhawat.models.UtilRegistry.{
  ActionPerformed,
  ActionPerformedWithError,
  ActionPerformedWithSuccess
}
import com.raunakjodhawat.models.{
  DebugToken,
  DebugTokenData,
  DebugTokenError,
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
  implicit val actionPerformedWithSuccessJsonFormat = jsonFormat1(
    ActionPerformedWithSuccess
  )
  implicit val actionPerformedWithErrorJsonFormat = jsonFormat1(
    ActionPerformedWithError
  )

  implicit val scheduleJsonFormat = jsonFormat2(Schedule)
  implicit val habitJsonFormat = jsonFormat6(Habit)
  implicit val habitsJsonFormat = jsonFormat1(Habits)

  implicit val workLogJSONFormat = jsonFormat5(WorkLog)
  implicit val workLogsJSONFormat = jsonFormat1(WorkLogs)

  implicit val changeLogJsonFormat = jsonFormat2(EditHistory)
  implicit val habitChangeLogJsonFormat = jsonFormat4(HabitEdit)
  implicit val habitChangeLogsJsonFormat = jsonFormat1(HabitEdits)

  implicit val fbDebugTokenErrorJsonFormat = jsonFormat2(DebugTokenError)
  implicit val fbDebugTokenDataJsonFormat = jsonFormat3(DebugTokenData)
  implicit val fbDebugTokenJsonFormat = jsonFormat1(DebugToken)
}
