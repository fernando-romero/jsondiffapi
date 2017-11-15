package com.example

/**
 * Describes the location and lentgh of the diff between two strings.
 *
 * @constructor creates a new Diff with offset and length.
 * @param offset position at the start of the diff.
 * @param length size of the diff.
 */
case class Diff(offset: Int, length: Int)