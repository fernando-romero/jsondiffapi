package com.example

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._
import java.util.Base64

class RouteServiceSpec extends Specification with Specs2RouteTest
    with RouteService with InsightService with StorageService {

  isolated

  import spray.json._
  import spray.json.DefaultJsonProtocol._
  import spray.httpx.SprayJsonSupport._
  import Protocols._

  def actorRefFactory = system

  "/v1/diff/:id" >> {
    "when left exists but right doesn't" >> {
      leftMap += ("test" -> "foobar")
      rightMap = Map.empty[String, String]
      "should return NotFound" >> {
        Get("/v1/diff/test") ~> route ~> check {
          status === NotFound
        }
      }
    }
    "when right exists but left doesn't" >> {
      leftMap = Map.empty[String, String]
      rightMap += ("test" -> "foobar")
      "should return NotFound" >> {
        Get("/v1/diff/test") ~> route ~> check {
          status === NotFound
        }
      }
    }
    "when left and right exist" >> {
      leftMap += ("test" -> "foobar")
      rightMap += ("test" -> "foobar")
      "should return Ok with Insight data" >> {
        Get("/v1/diff/test") ~> route ~> check {
          status === OK
          responseAs[Insight].areEqual must beTrue
        }
      }
    }
  }
  "/v1/diff/:id/left" >> {
    "when json data is not base64 encoded" >> {
      val body = HttpEntity(MediaTypes.`application/json`, """{"foo":"bar"}""")
      "should return UnsupportedMediaType" >> {
        Post("/v1/diff/test/left", body) ~> sealRoute(route) ~> check {
          status == UnsupportedMediaType
        }
      }
    }
    "when json data is base64 enconded" >> {
      val bytes = Base64.getEncoder.encode("""{"foo":"bar"}""".getBytes)
      val body = HttpEntity(MediaTypes.`application/base64`, bytes)
      "should return NoContent and update data" >> {
        Post("/v1/diff/test/left", body) ~> route ~> check {
          status == NoContent
          leftMap.get("test") must beSome("""{"foo":"bar"}""")
        }
      }
    }
  }
  "/v1/diff/:id/right" >> {
    "when json data is not base64 encoded" >> {
      val body = HttpEntity(MediaTypes.`application/json`, """{"foo":"bar"}""")
      "should return UnsupportedMediaType" >> {
        Post("/v1/diff/test/right", body) ~> sealRoute(route) ~> check {
          status == UnsupportedMediaType
        }
      }
    }
    "when json data is base64 enconded" >> {
      val bytes = Base64.getEncoder.encode("""{"foo":"bar"}""".getBytes)
      val body = HttpEntity(MediaTypes.`application/base64`, bytes)
      "should return NoContent and update data" >> {
        Post("/v1/diff/test/right", body) ~> route ~> check {
          status == NoContent
          rightMap.get("test") must beSome("""{"foo":"bar"}""")
        }
      }
    }
  }
}
