package com.example

/** Provides the method used to calculate an insight. */
trait InsightService {

  /**
   * Calculates an insight.
   *
   * @param left left string.
   * @param right right string.
   * @return an [[com.example.Insight]] instance.
   */
  def getInsight(left: String, right: String): Insight = {
    if (left == right) {
      Insight(areEqual = true, areEqualSize = true)
    } else if (left.size != right.size) {
      Insight(areEqual = false, areEqualSize = false)
    } else {
      var index = 0
      var offset = 0
      var length = 0
      var diffs: List[Diff] = List.empty
      for ((lChar, rChar) <- (left zip right)) yield {
        if (lChar == rChar && length > 0) {
          diffs = Diff(offset, length) :: diffs
          length = 0
        }
        if (lChar != rChar) {
          if (length == 0) {
            offset = index
          }
          length += 1
        }
        index += 1
      }
      if (length > 0) {
        diffs = Diff(offset, length) :: diffs
      }
      Insight(areEqual = false, areEqualSize = true, diffs = diffs)
    }
  }
}