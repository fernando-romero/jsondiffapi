package com.example

import akka.actor.Actor
import scala.util.Properties
import spray.http._
import spray.routing._

/** Actor used by Spray as HttpService. */
class ApiActor extends Actor with RouteService with InsightService with StorageService with MongoService {
  val data = {
    val host = Properties.envOrElse("MONGO_HOST", "localhost")
    connect(host, "jsondiffapi")
  }
  def actorRefFactory = context
  def receive = runRoute(route)
}