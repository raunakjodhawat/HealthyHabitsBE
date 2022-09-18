package com.raunakjodhawat.utilties.token

import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.util.ByteString
import com.raunakjodhawat.models.DebugToken

import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import com.raunakjodhawat.routes.JsonFormats._
import akka.actor.typed.ActorSystem

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration

case object TokenUtility {
  implicit val system = ActorSystem(Behaviors.empty, "singleRequest")
  implicit val executionContext = system.executionContext

  val FB_GRAPH_API_URL = "https://graph.facebook.com/v14.0"
  val FB_CLIENT_ID = System.getenv("FB_CLIENT_ID")
  val FB_CLIENT_SECRET = System.getenv("FB_CLIENT_SECRET")

  def isTokenValid(
      u_id: String,
      a_t: String
  ): Boolean = {
    val url =
      s"$FB_GRAPH_API_URL/debug_token?input_token=$a_t&access_token=$FB_CLIENT_ID|$FB_CLIENT_SECRET"
    val fbFutureResponse: Future[HttpResponse] =
      Http().singleRequest(Get(uri = url))

    try {
      val fbResponse: HttpResponse = {
        Await.result(fbFutureResponse, Duration(5, TimeUnit.SECONDS))
      }
      val debugTokenFuture: Future[DebugToken] = Await.result(
        fbResponse.entity.dataBytes
          .runFold("")(_ ++ _.utf8String)
          .map(body => Unmarshal(body).to[DebugToken]),
        Duration(5, TimeUnit.SECONDS)
      )
      val debugToken =
        Await.result(debugTokenFuture, Duration(5, TimeUnit.SECONDS))
      debugToken.data.user_id.getOrElse("").equals(u_id)
    } catch {
      case _ => false
    }
  }

  def generateSecretKey(
      user_id: Option[String],
      token: Option[String]
  ): Option[String] = {
    (user_id, token) match {
      case (Some(u_id), Some(t_id)) if isTokenValid(u_id, t_id) =>
        // some logic to generate secret key
        Some("random")
      case _ => None
    }
  }
}
