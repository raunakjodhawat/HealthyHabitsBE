package com.raunakjodhawat

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{Directives, Route}

import scala.util.Failure
import scala.util.Success
import com.raunakjodhawat.routes.{HabitRoutes, UserRoutes, WorkLogRoutes}

object Application {
  private def startHttpServer(
      routes: Route
  )(implicit system: ActorSystem[_]): Unit = {
    import system.executionContext

    val futureBinding = Http().newServerAt("localhost", 8080).bind(routes)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info(
          "Server online at http://{}:{}/",
          address.getHostString,
          address.getPort
        )
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }

  def main(args: Array[String]): Unit = {
    val rootBehavior = Behaviors.setup[Nothing] { context =>
      val userRegistryActor =
        context.spawn(models.UserRegistry(), "UserRegistryActor")

      val habitRegistryActor =
        context.spawn(models.HabitRegistry(), "HabitRegistryActor")

      val workLogRegistryActor =
        context.spawn(models.WorkLogRegistry(), "WorkLogRegistryActor")

      context.watch(habitRegistryActor)
      context.watch(userRegistryActor)
      context.watch(workLogRegistryActor)

      val userRoutes = new UserRoutes(userRegistryActor)(context.system)
      val habitRoutes = new HabitRoutes(habitRegistryActor)(context.system)
      val workLogRoutes =
        new WorkLogRoutes(workLogRegistryActor)(context.system)

      val allRoutes: Route =
        Directives.concat(
          userRoutes.userRoutes,
          habitRoutes.habitRoutes,
          workLogRoutes.workLogRoutes
        )
      startHttpServer(allRoutes)(context.system)
      Behaviors.empty
    }
    val system = ActorSystem[Nothing](rootBehavior, "HelloAkkaHttpServer")
  }
}
