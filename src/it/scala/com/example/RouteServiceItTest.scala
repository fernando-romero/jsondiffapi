package com.example

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._
import java.util.Base64
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Properties

class RouteServiceItTest extends Specification with Specs2RouteTest
    with RouteService with InsightService with StorageService
    with MongoService {

  isolated

  import spray.json._
  import spray.json.DefaultJsonProtocol._
  import spray.httpx.SprayJsonSupport._
  import Protocols._

  def actorRefFactory = system

  val data = {
    val host = Properties.envOrElse("MONGO_HOST_TEST", "localhost")
    connect(host, "jsondiffapi-test")
  }

  val dur = Duration(5, SECONDS)
  Await.ready(data.map(col => col.drop(false)), dur)

  "when left and right are equal should return insight with: areEqual = true, areEqualSize = true and no diffs" >> {

    val bytes = Base64.getEncoder.encode("""{"foo":"bar"}""".getBytes)
    val body = HttpEntity(MediaTypes.`application/base64`, bytes)

    Post("/v1/diff/test1/left", body) ~> route ~> check {
      status == NoContent
    }

    Post("/v1/diff/test1/right", body) ~> route ~> check {
      status == NoContent
    }

    Get("/v1/diff/test1") ~> route ~> check {
      status === OK
      val ins = responseAs[Insight]
      ins.areEqual must beTrue
      ins.areEqualSize must beTrue
      ins.diffs.size must beEqualTo(0)
    }
  }

  "when left and right are different sizes should return insight with: areEqual = false, areEqualSize = false and no diffs" >> {

    val bytesLeft = Base64.getEncoder.encode("""{"foo":"bar"}""".getBytes)
    val bodyLeft = HttpEntity(MediaTypes.`application/base64`, bytesLeft)

    Post("/v1/diff/test2/left", bodyLeft) ~> route ~> check {
      status == NoContent
    }

    val bytesRight = Base64.getEncoder.encode("""{"foo":"bartolomeo"}""".getBytes)
    val bodyRight = HttpEntity(MediaTypes.`application/base64`, bytesRight)

    Post("/v1/diff/test2/right", bodyRight) ~> route ~> check {
      status == NoContent
    }

    Get("/v1/diff/test2") ~> route ~> check {
      status === OK
      val ins = responseAs[Insight]
      ins.areEqual must beFalse
      ins.areEqualSize must beFalse
      ins.diffs.size must beEqualTo(0)
    }
  }

  "when left and right are different but same sizes should return insight with: areEqual = false, areEqualSize = true and diffs with offset and length data" >> {

    val bytesLeft = Base64.getEncoder.encode("""{"foo":"bar","ying":"yang","scala":"java"}""".getBytes)
    val bodyLeft = HttpEntity(MediaTypes.`application/base64`, bytesLeft)

    Post("/v1/diff/test3/left", bodyLeft) ~> route ~> check {
      status == NoContent
    }

    val bytesRight = Base64.getEncoder.encode("""{"fii":"mee","ying":"yang","java":"scala"}""".getBytes)
    val bodyRight = HttpEntity(MediaTypes.`application/base64`, bytesRight)

    Post("/v1/diff/test3/right", bodyRight) ~> route ~> check {
      status == NoContent
    }

    Get("/v1/diff/test3") ~> route ~> check {
      status === OK
      val ins = responseAs[Insight]
      ins.areEqual must beFalse
      ins.areEqualSize must beTrue
      ins.diffs.size must beEqualTo(4)
      ins.diffs(0).offset must beEqualTo(38)
      ins.diffs(0).length must beEqualTo(1)
      ins.diffs(1).offset must beEqualTo(28)
      ins.diffs(1).length must beEqualTo(9)
      ins.diffs(2).offset must beEqualTo(8)
      ins.diffs(2).length must beEqualTo(3)
      ins.diffs(3).offset must beEqualTo(3)
      ins.diffs(3).length must beEqualTo(2)
    }
  }
}
