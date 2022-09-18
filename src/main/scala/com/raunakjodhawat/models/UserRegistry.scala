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
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.util.ByteString
import com.raunakjodhawat.utilties.token.TokenUtility

import scala.concurrent.Future
import scala.util.{Failure, Success}

final case class User(
    fb_user_id: String,
    fb_access_token: Option[String],
    fb_secret_key: Option[String]
)
final case class Users(users: immutable.Seq[User])

object UserRegistry {
  final case class LoginUser(user: User, replyTo: ActorRef[ActionPerformed])
      extends Command

  final case class LogoutUser(user: User, replyTo: ActorRef[ActionPerformed])
      extends Command

  def apply(): Behavior[Command] = registry(Seq.empty)

  private def registry(users: Seq[User]): Behavior[Command] =
    Behaviors.receiveMessage {
      case LoginUser(user, replyTo) =>
        user.fb_access_token match {
          case Some(a_t) => {
            val isValidToken: Boolean = {
              TokenUtility.isTokenValid(user.fb_user_id, a_t)
            }
            println(isValidToken)
          }
          case _ => {
            println("Access token error")
          }
        }
        users.find(p => p.fb_user_id == user.fb_user_id) match {
          case Some(_) =>
            {
              replyTo ! ActionPerformedWithError(
                s"${user.fb_user_id} is already created"
              )
            }
            Behaviors.same
          case _ => {
            replyTo ! ActionPerformedWithSuccess(
              s"${user.fb_user_id} is created"
            )
            registry(users :+ user)
          }
        }
      case LogoutUser(user, replyTo) =>
        users.find(_.fb_user_id == user.fb_user_id) match {
          case Some(_) => {
            replyTo ! ActionPerformedWithSuccess(
              s"${user.fb_user_id} is updated"
            )
            registry(
              users.filterNot(p => p.fb_user_id == user.fb_user_id) :+ user
            )
          }
          case _ => {
            replyTo ! ActionPerformedWithError(
              s"${user.fb_user_id} is not found"
            )
            Behaviors.same
          }
        }

    }
}
