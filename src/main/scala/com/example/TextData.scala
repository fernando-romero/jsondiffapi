package com.example

/**
 * Contains the data to be saved to the database.
 *
 * @constructor creates a new TextData.
 * @param id id of the object. Is an unique identifier.
 * @param left the left data to be stored.
 * @param right the right data to be stored.
 */
case class TextData(id: String, left: Option[String], right: Option[String])