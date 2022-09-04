package com.raunakjodhawat.routes

import com.raunakjodhawat.models.UtilRegistry.ActionPerformed
import com.raunakjodhawat.models.{Habit, Schedule, User, Users}
import spray.json.DefaultJsonProtocol
object JsonFormats {
  import DefaultJsonProtocol._

  implicit val userJsonFormat = jsonFormat3(User)
  implicit val usersJsonFormat = jsonFormat1(Users)
  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)

  implicit val scheduleJsonFormat = jsonFormat2(Schedule)
  implicit val habitJsonFormat = jsonFormat6(Habit)

}
