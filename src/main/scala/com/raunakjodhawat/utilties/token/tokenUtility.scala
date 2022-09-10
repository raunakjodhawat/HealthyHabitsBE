package com.raunakjodhawat.utilties.token

import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.util.ByteString

import scala.collection.immutable
import scala.concurrent.Future
import scala.util.{Failure, Success}

// refrences: https://developers.facebook.com/docs/graph-api/reference/v14.0/debug_token
case class DebugTokenError(
    code: Int,
    message: String,
    subcode: Int
)
case class DebugTokenData(
    error: Option[DebugTokenError],
    app_id: String,
    `type`: Option[String],
    application: Option[String],
    expires_at: Option[Int],
    data_access_expires_at: Option[Int],
    is_valid: Option[Boolean],
    scopes: immutable.Seq[String],
    user_id: Option[String]
)
case class DebugToken(data: DebugTokenData)

case object tokenUtility extends App {
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import spray.json.DefaultJsonProtocol._
  val FB_GRAPH_API_URL = "https://graph.facebook.com/v14.0"
  implicit val frm1 = jsonFormat3(DebugTokenError)
  implicit val frm2 = jsonFormat9(DebugTokenData)
  implicit val frm3 = jsonFormat1(DebugToken)

  import akka.actor.typed.ActorSystem
  implicit val system = ActorSystem(Behaviors.empty, "singleRequest")
  implicit val executionContext = system.executionContext

  val a =
    "sdfsdf"

  val app_access_token =
    "sdfsd"
  val res = functionCreateSecretKey(Some("asd"), Some(a))
  println(res)
  def functionCreateSecretKey(
      user_id: Option[String],
      access_token: Option[String]
  ): Option[String] = {
    (user_id, access_token) match {
      case (Some(u_id), Some(a_t)) => {
        // query fb
        val url =
          s"$FB_GRAPH_API_URL/debug_token?input_token=$a_t&access_token=$a_t"
        val fbResponse: Future[HttpResponse] =
          Http().singleRequest(Get(uri = url))

        fbResponse.onComplete {
          case Success(response) => {
            response.entity.dataBytes
              .runFold(ByteString(""))(_ ++ _)
              .foreach { body =>
                println(body.utf8String)
                Unmarshal(
                  body.utf8String
                ).to[DebugToken].onComplete {
                  case Success(v) => println(v)
                  case Failure(e) => println(e)
                }
              }

          }
          case Failure(error) => {
            println(error)
          }
        }
        Some("hello")
      }
      case _ => None
    }
  }
}
