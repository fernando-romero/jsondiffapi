package com.example

import spray.json._

/** Provides spray-json serialization for case classes. */
object Protocols extends DefaultJsonProtocol {
  implicit val diffFormat = jsonFormat2(Diff)
  implicit val insightFormat = jsonFormat3(Insight)
}