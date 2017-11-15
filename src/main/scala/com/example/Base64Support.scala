package com.example

import java.util.Base64
import spray.http.{ HttpEntity, MediaTypes }
import spray.httpx.unmarshalling._
import spray.httpx.unmarshalling.UnmarshallerLifting._
import spray.json._
import spray.json.DefaultJsonProtocol._

/** Provides an unmarshaller to spray can extract base64 encoded json values. */
trait Base64Support {
  implicit val Base64JsValueUnmarshaller: FromRequestUnmarshaller[JsValue] = {
    fromRequestUnmarshaller {
      fromMessageUnmarshaller {
        Unmarshaller[JsValue](MediaTypes.`application/base64`) {
          case x: HttpEntity.NonEmpty => {
            val s = new String(Base64.getDecoder.decode(x.asString.trim), "UTF-8")
            s.parseJson
          }
        }
      }
    }
  }
}