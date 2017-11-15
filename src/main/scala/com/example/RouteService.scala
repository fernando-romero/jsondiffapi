// format: OFF

package com.example

import akka.actor.Actor
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success
import spray.http._
import spray.http.MediaTypes._
import spray.json._
import spray.routing._

/** Provides all the http routing of the application. */
trait RouteService extends HttpService with Base64Support { self: InsightService with StorageService =>

  import spray.json._
  import spray.json.DefaultJsonProtocol._
  import spray.httpx.SprayJsonSupport._
  import Protocols._

  val route = {
    pathPrefix("v1" / "diff" / Segment) { id =>
      pathEnd { ctx =>
        val f: Future[Option[Insight]] = for {
          leftOpt <- getLeft(id)
          rightOpt <- getRight(id)
        } yield {
          for {
            left <- leftOpt
            right <- rightOpt
          } yield {
            getInsight(left, right)
          }
        }
        f.onComplete {
          case Success(iOpt) => iOpt match {
            case Some(i) => ctx.complete(i)
            case None => ctx.complete(StatusCodes.NotFound)
          }
          case Failure(e) => ctx.complete(StatusCodes.InternalServerError)
        }
      } ~
      entity(as[JsValue]) { json =>
        path("left") { ctx =>
          val f = setLeft(id, json.compactPrint)
          f.onComplete {
            case Success(_) => ctx.complete(StatusCodes.NoContent)
            case Failure(e) => ctx.complete(StatusCodes.InternalServerError)
          }
        } ~
        path("right") { ctx =>
          val f = setRight(id, json.compactPrint)
          f.onComplete {
            case Success(_) => ctx.complete(StatusCodes.NoContent)
            case Failure(e) => ctx.complete(StatusCodes.InternalServerError)
          }
        }
      }
    }
  }
}