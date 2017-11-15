package com.example

import org.specs2.mutable.Specification

class StorageServiceSpec extends Specification with StorageService {
  "StorageService#setLeft" >> {
    "should return the value set" >> {
      setLeft("test", "foobar") must beEqualTo("foobar").await
      leftMap.get("test") must beSome("foobar")
    }
  }
  "StorageService#getLeft" >> {
    "should return left value for the given id" >> {
      leftMap += ("test" -> "foobar")
      getLeft("test") must beSome("foobar").await
    }
  }
  "StorageService#setRight" >> {
    "should return the value set" >> {
      setRight("test", "foobar") must beEqualTo("foobar").await
      rightMap.get("test") must beSome("foobar")
    }
  }
  "StorageService#getRight" >> {
    "should return right value for the given id" >> {
      rightMap += ("test" -> "foobar")
      getRight("test") must beSome("foobar").await
    }
  }
}