package com.raunakjodhawat.routes

import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.raunakjodhawat.models.UtilRegistry.{ActionPerformed, Command}
import com.raunakjodhawat.models.{WorkLog, WorkLogs}
import com.raunakjodhawat.models.WorkLogRegistry.{
  AddWorkLog,
  DeleteWorkLog,
  EditWorkLog,
  GetWorkLog
}

import scala.concurrent.Future

class WorkLogRoutes(workLogRegistry: ActorRef[Command])(implicit
    val system: ActorSystem[_]
) {
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._

  implicit val timeout = Timeout.create(
    system.settings.config.getDuration("my-app.routes.ask-timeout")
  )

  def createWorkLog(workLog: WorkLog): Future[ActionPerformed] =
    workLogRegistry.ask(AddWorkLog(workLog, _))

  def editWorkLog(workLog: WorkLog): Future[WorkLog] =
    workLogRegistry.ask(EditWorkLog(workLog, _))

  def deleteWorkLog(workLog: WorkLog): Future[ActionPerformed] =
    workLogRegistry.ask(DeleteWorkLog(workLog, _))

  def getAllWorkLogs(userId: Int): Future[WorkLogs] =
    workLogRegistry.ask(GetWorkLog(userId, _))

  val workLogRoutes: Route = path("worklog") {
    concat(
      get {
        parameter("userId".as[Int]) { (userId) =>
          {
            val allWorkLogs = getAllWorkLogs(userId)
            complete(200, allWorkLogs)
          }
        }
      },
      delete {
        entity(as[WorkLog]) { (workLog) =>
          {
            val deletedWorkLog = deleteWorkLog(workLog)
            complete(200, "deletedWorkLog")
          }
        }
      },
      put {
        entity(as[WorkLog]) { (workLog) =>
          {
            val editedWorkLog = editWorkLog(workLog)
            complete(200, editedWorkLog)
          }
        }
      },
      post {
        entity(as[WorkLog]) { (workLog) =>
          {
            val newWorkLog = createWorkLog(workLog)
            complete(200, "newWorkLog")
          }
        }
      }
    )
  }
}
