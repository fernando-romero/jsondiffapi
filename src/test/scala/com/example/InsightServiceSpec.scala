package com.example

import org.specs2.mutable.Specification

class InsightServiceSpec extends Specification with InsightService {
  "InsightService#getInsight" >> {
    "when left and right" >> {
      "are equal should return an Insight object with" >> {
        val insight = getInsight("foobar", "foobar")
        "areEqual = true" >> {
          insight.areEqual must beTrue
        }
        "areEqualSize = true" >> {
          insight.areEqualSize must beTrue
        }
        "and diffs empty" >> {
          insight.diffs.size must beEqualTo(0)
        }
      }
      "are not the same size should return an Insight object with" >> {
        val insight = getInsight("foobar", "foobarbaz")
        "areEqual = false" >> {
          insight.areEqual must beFalse
        }
        "areEqualSize = false" >> {
          insight.areEqualSize must beFalse
        }
        "and diffs empty" >> {
          insight.diffs.size must beEqualTo(0)
        }
      }
      "are the same size should return an Insight object with" >> {
        val insight = getInsight("foobarbaz", "feebarmee")
        "areEqual = false" >> {
          insight.areEqual must beFalse
        }
        "areEqualSize = true" >> {
          insight.areEqualSize must beTrue
        }
        "and diffs contains the offsets and lengths" >> {
          insight.diffs.size must beEqualTo(2)
          // foobarBAZ - feebarMEE
          insight.diffs(0).offset must beEqualTo(6)
          insight.diffs(0).length must beEqualTo(3)
          // fOObarbaz - fEEbarmee
          insight.diffs(1).offset must beEqualTo(1)
          insight.diffs(1).length must beEqualTo(2)
        }
      }
    }
  }
}
