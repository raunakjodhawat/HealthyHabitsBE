package com.raunakjodhawat.models

import akka.actor.typed.{ActorRef, Behavior}
import com.raunakjodhawat.models.UtilRegistry.{ActionPerformed, Command}

import scala.collection.immutable
import akka.actor.typed.scaladsl.Behaviors

final case class User(user_id: Int, email: String, user_name: String)
final case class Users(users: immutable.Seq[User])

object UserRegistry {
  final case class CreateUser(user: User, replyTo: ActorRef[ActionPerformed])
      extends Command

  final case class GetAllUsers(replyTo: ActorRef[Users]) extends Command

  def apply(): Behavior[Command] = registry(Seq.empty)

  private def registry(users: Seq[User]): Behavior[Command] =
    Behaviors.receiveMessage {
      case CreateUser(user, replyTo) =>
        replyTo ! ActionPerformed(
          s"${user.user_name} with ${user.user_id} is created"
        )
        registry(users :+ user)
      case GetAllUsers(replyTo) =>
        replyTo ! Users(users)
        Behaviors.same
    }
}
