package com.example

import akka.actor.{ ActorSystem, Props }
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import spray.can.Http
import scala.concurrent.duration._

/** Application's starting class. */
object Boot extends App {
  implicit val system = ActorSystem()
  val apiActor = system.actorOf(Props[ApiActor])
  implicit val timeout = Timeout(5.seconds)
  IO(Http) ? Http.Bind(apiActor, interface = "0.0.0.0", port = 8080)
}
