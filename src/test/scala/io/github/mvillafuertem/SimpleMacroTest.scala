package io.github.mvillafuertem

import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

final class SimpleMacroTest extends AnyWordSpecLike with Matchers with BeforeAndAfterAll {

  "SimpleMacro" should {

    "WIP" in {

      val str = SimpleMacro.defOddEvenMacro(9)
      println(str)
      println(str)
      println(str)

      val data: SimpleMacro.RemoteData = SimpleMacro.RemoteData(
        symbol = "APPLE",
        strikePrice = 9,
        underlyingPrice = 9
      )
      val result = SimpleMacro.query(data.underlyingPrice)
      println(result)
      println(result)
      println(result)
      println(result)

    }
  }

}
