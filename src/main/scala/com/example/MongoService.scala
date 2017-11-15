package com.example

import reactivemongo.api._
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDocumentReader
import reactivemongo.bson.BSONDocumentWriter
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** Provides a MongoDB implementation of StorageService. */
trait MongoService extends StorageService {

  val data: Future[BSONCollection]

  /**
   * Connects to MongoDB.
   *
   * @param host where mongodb is running.
   * @param dbName name of the database.
   * @return a Future to the data collection.
   */
  def connect(host: String, dbName: String) = {
    val driver = new MongoDriver
    val connection = driver.connection(List(host))
    val db = connection.database(dbName)
    db.map(_("data"))
  }

  /** Provides a BSON to TextData converter. */
  implicit object TextDataReader extends BSONDocumentReader[TextData] {
    def read(bson: BSONDocument): TextData = {
      bson.getAs[String]("_id").map { id =>
        val left = bson.getAs[String]("left")
        val right = bson.getAs[String]("right")
        TextData(id, left, right)
      }.get // fail if uncapable of read.
    }
  }

  /** Provides a TextData to BSON converter. */
  implicit object TextDataWriter extends BSONDocumentWriter[TextData] {
    def write(textData: TextData): BSONDocument = {
      BSONDocument(
        "_id" -> textData.id,
        "left" -> textData.left, "right" -> textData.right
      )
    }
  }

  /**
   * Gets left data from MongoDB for the given id.
   *
   * @param id id of the data
   * @return a Future to an Option with the data
   */
  override def getLeft(id: String): Future[Option[String]] = {
    data.flatMap { col =>
      val query = BSONDocument("_id" -> id)
      col.find(query).one[TextData].map { tdOpt =>
        tdOpt.flatMap(td => td.left)
      }
    }
  }

  /**
   * Gets right data from MongoDB for the given id.
   *
   * @param id id of the data
   * @return a Future to an Option with the data
   */
  override def getRight(id: String): Future[Option[String]] = {
    data.flatMap { col =>
      val query = BSONDocument("_id" -> id)
      col.find(query).one[TextData].map { tdOpt =>
        tdOpt.flatMap(td => td.right)
      }
    }
  }

  /**
   * Sets left data to MongoDB for the given id.
   *
   * @param id id of the data
   * @param text data to be stored
   * @return a Future to the data stored
   */
  override def setLeft(id: String, text: String): Future[String] = {
    data.flatMap { col =>
      val selector = BSONDocument("_id" -> id)
      val modifier = BSONDocument("$set" -> BSONDocument("left" -> text))
      col.update(selector, modifier, upsert = true).map(_ => text)
    }
  }

  /**
   * Sets right data to MongoDB for the given id.
   *
   * @param id id of the data
   * @param text data to be stored
   * @return a Future to the data stored
   */
  override def setRight(id: String, text: String): Future[String] = {
    data.flatMap { col =>
      val selector = BSONDocument("_id" -> id)
      val modifier = BSONDocument("$set" -> BSONDocument("right" -> text))
      col.update(selector, modifier, upsert = true).map(_ => text)
    }
  }
}