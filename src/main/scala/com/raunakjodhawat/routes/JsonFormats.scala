package com.raunakjodhawat.routes

import com.raunakjodhawat.models.UtilRegistry.ActionPerformed
import com.raunakjodhawat.models.{User, Users}
import spray.json.DefaultJsonProtocol
object JsonFormats {
  import DefaultJsonProtocol._

  implicit val userJsonFormat = jsonFormat3(User)
  implicit val usersJsonFormat = jsonFormat1(Users)
  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)

}
