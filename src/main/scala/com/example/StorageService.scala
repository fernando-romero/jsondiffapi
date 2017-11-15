package com.example

import scala.concurrent.Future

/** Provides an api for the storage and a default in-memory implementation for unit tests. */
trait StorageService {

  var leftMap = Map.empty[String, String]
  var rightMap = Map.empty[String, String]

  /**
   * Gets left data for the given id.
   *
   * @param id id of the data
   * @return a Future to an Option with the data
   */
  def getLeft(id: String): Future[Option[String]] = {
    Future.successful(leftMap.get(id));
  }

  /**
   * Gets right data for the given id.
   *
   * @param id id of the data
   * @return a Future to an Option with the data
   */
  def getRight(id: String): Future[Option[String]] = {
    Future.successful(rightMap.get(id));
  }

  /**
   * Sets left data for the given id.
   *
   * @param id id of the data
   * @param text data to be stored
   * @return a Future to the data stored
   */
  def setLeft(id: String, text: String): Future[String] = {
    leftMap += (id -> text)
    Future.successful(text)
  }

  /**
   * Sets right data for the given id.
   *
   * @param id id of the data
   * @param text data to be stored
   * @return a Future to the data stored
   */
  def setRight(id: String, text: String): Future[String] = {
    rightMap += (id -> text)
    Future.successful(text)
  }
}