package io.github.mvillafuertem

import scala.language.experimental.macros

object SimpleAST {

  trait Ast[T]
  case class Subtract(a: Ast[Int], b: Ast[Int])     extends Ast[Int]
  case class Concat(a: Ast[String], b: Ast[String]) extends Ast[String]
  case class ToString(value: Ast[Int])              extends Ast[String]
  case class Constant(value: String)                extends Ast[String]
  case class Key(name: String)                      extends Ast[String]

  case class RemoteData(
    symbol: String,
    strikePrice: Int,
    underlyingPrice: Int
  )

  def execute(data: RemoteData) =
    (data.underlyingPrice - data.strikePrice).toString + "/" + data.symbol

}
