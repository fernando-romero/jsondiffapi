package com.example

/**
 * Describes the differences between two same length strings.
 *
 * @constructor creates a new Insigth with default values.
 * @param areEqual determines if strings are equal.
 * @param areEqualSize determines if strings hase same size.
 * @param diffs list of diffs found.
 */
case class Insight(
  areEqual: Boolean = false,
  areEqualSize: Boolean = false,
  diffs: List[Diff] = List.empty
)