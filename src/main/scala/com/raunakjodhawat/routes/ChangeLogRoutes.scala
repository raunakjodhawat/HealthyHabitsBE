package com.raunakjodhawat.routes

import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directives, Route}
import akka.util.Timeout
import com.raunakjodhawat.models.ChangeLogRegistry.{GetAllChangeLogs}
import com.raunakjodhawat.models.{
  ChangeLogs,
  Habit,
  HabitChange,
  WorkLogChange,
  changeValues
}
import com.raunakjodhawat.models.UtilRegistry.{ActionPerformed, Command}

import scala.concurrent.Future

//class ChangeLogRoutes(changeLogRegistry: ActorRef[Command])(implicit
//    val system: ActorSystem[_]
//) {
//  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
//  import JsonFormats._
//
//  private implicit val timeout = Timeout.create(
//    system.settings.config.getDuration("my-app.routes.ask-timeout")
//  )
//
//  def getAllChangeLogsForAUser(userId: Int): Future[ChangeLogs] =
//    changeLogRegistry.ask(GetAllChangeLogs(userId, _))
//
//  def saveChangeLog[A](
//      habitId: Int,
//      userId: Int,
//      newValue: changeValues[A],
//      oldValue: Option[changeValues[A]]
//  ): Future[ActionPerformed] = {
//    A match {
//      case
//    }
//    newValue match {
//      case Habit(x) =>
//    }
//    AddNewChangeLog(userId, habitId, habi)
//    newValue match {
//      case HabitEdits(newHabit) =>
//        AddNewChangeLog(
//          userId,
//          habitId,
//          HabitChange(oldValue, newHabit),
//          None,
//          _
//        )
//    }
//    newHabit match {
//      case Some(newH) =>
//        changeLogRegistry.ask(
//          AddNewChangeLog(
//            userId,
//            habitId,
//            Some(HabitChange(oldHabit, newH)),
//            None,
//            _
//          )
//        )
//      case _ => {
//        newWorkLog match {
//          case Some(newW) =>
//            changeLogRegistry.ask(
//              AddNewChangeLog(
//                userId,
//                habitId,
//                None,
//                Some(WorkLogChange(oldWorkLog, newW)),
//                _
//              )
//            )
//          case _ =>
//            changeLogRegistry.ask(
//              AddNewChangeLog(userId, habitId, None, None, _)
//            )
//        }
//      }
//    }
//  }
//
//  val changeLogRoutes: Route = {
//    path("changelog") {
//      concat(
//        get {
//          parameter("userId".as[Int]) { (userId) =>
//            {
//              val allChanges = getAllChangeLogsForAUser(userId)
//              complete(200, allChanges)
//            }
//          }
//        },
//        post {
//          parameters("userId".as[Int], "habitId".as[Int]) { (userId, habitId) =>
//            {
//              entity(as[Option[Habit]]) { habit =>
//                {
//                  en
//                }
//              }
//              val changeLogAdded = saveChangeLog(userId, habitId)
//            }
//          }
//        }
//      )
//    }
//  }
//}
