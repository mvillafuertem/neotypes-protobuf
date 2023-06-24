package io.github.mvillafuertem

import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

final class SimpleMacroTest extends AnyWordSpecLike with Matchers with BeforeAndAfterAll {

  "SimpleMacro" should {

    "WIP" in {

//      val str = SimpleMacro.defOddEvenMacro(9)
//      println(str)
//      println(str)
//      println(str)

      val mydata: SimpleMacro.RemoteData = SimpleMacro.RemoteData(
        symbol = "APPLE",
        strikePrice = 9,
        underlyingPrice = 9
      )
      val result = SimpleMacro.query(mydata.underlyingPrice)
      println(result)
      val result2: SimpleMacro.Ast = SimpleMacro.query(mydata.underlyingPrice - mydata.strikePrice)
      println("result2result2result2")
      println(result2)
      println(result2)
      println(result2)

      val number = Macros.nonZeroNum(8)
      println(number)
      println(number)
      println(number)
      println(number)

    }
  }

}
