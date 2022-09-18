package com.raunakjodhawat.models

import akka.actor.typed.{ActorRef, Behavior}
import com.raunakjodhawat.models.UtilRegistry.{
  ActionPerformed,
  ActionPerformedWithError,
  ActionPerformedWithSuccess,
  Command
}

import scala.collection.immutable
import akka.actor.typed.scaladsl.Behaviors

final case class User(
    user_id: Int,
    access_token: Option[String],
    secret_key: Option[String]
)
final case class Users(users: immutable.Seq[User])

object UserRegistry {
  final case class CreateUser(user: User, replyTo: ActorRef[ActionPerformed])
      extends Command

  final case class GetAUser(user_id: Int, replyTo: ActorRef[Option[User]])
      extends Command

  final case class UpdateAUser(user: User, replyTo: ActorRef[ActionPerformed])
      extends Command

  def apply(): Behavior[Command] = registry(Seq.empty)

  private def registry(users: Seq[User]): Behavior[Command] =
    Behaviors.receiveMessage {
      case CreateUser(user, replyTo) =>
        users.find(p => p.user_id == user.user_id) match {
          case Some(_) =>
            {
              replyTo ! ActionPerformedWithError(
                s"${user.user_id} is already created"
              )
            }
            Behaviors.same
          case _ => {
            replyTo ! ActionPerformedWithSuccess(
              s"${user.user_id} is created"
            )
            registry(users :+ user)
          }
        }
      case GetAUser(user_id: Int, replyTo) =>
        replyTo ! users.find(u => u.user_id == user_id)
        Behaviors.same
      case UpdateAUser(user, replyTo) =>
        users.find(_.user_id == user.user_id) match {
          case Some(_) => {
            replyTo ! ActionPerformedWithSuccess(
              s"${user.user_id} is updated"
            )
            registry(users.filterNot(p => p.user_id == user.user_id) :+ user)
          }
          case _ => {
            replyTo ! ActionPerformedWithError(
              s"${user.user_id} is not found"
            )
            Behaviors.same
          }
        }

    }
}
