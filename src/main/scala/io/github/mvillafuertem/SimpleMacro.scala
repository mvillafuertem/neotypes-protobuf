package io.github.mvillafuertem

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

// https://www.youtube.com/watch?v=8ryhJOE1m30
// https://www.youtube.com/watch?v=8scL8dzRqjA
// https://docs.scala-lang.org/overviews/reflection/symbols-trees-types.html
object SimpleMacro {

  case class RemoteData(
                         symbol: String,
                         strikePrice: Int,
                         underlyingPrice: Int
                       )

  def defOddEvenMacro(number: Int): String = macro defMacroImplRef

  def defMacroImplRef(
    c: blackbox.Context
  )(number: c.Expr[Int]): c.Expr[String] = {
    import c.universe._

    // val q"10 $op 20" = q"10 + 20"
    val Apply(
      Select(Literal(Constant(a: Int)), TermName(operation: String)),
      List(Literal(Constant(b: Int)))
    ) = q"10 + 20"

    val Literal(Constant(s_number: Int)) = number.tree

    val result = s_number % 2 match {
      case 0 => Literal(Constant(s"even $a $operation $b"))
      case _ => Literal(Constant(s"odd $a $operation $b"))
    }

    c.Expr[String](result)

  }


  def query(value: Any): String = macro queryImp

  def queryImp(
    c: blackbox.Context
  )(value: c.Expr[Any]): c.Expr[String] = {
    import c.universe._

    val value1 = reify {
      println(s"hello ${value.splice}!")
    }
    println(s"::::::::::::::::::::")
    println(showRaw(value))
    println(showRaw(value))
    println(showRaw(value))
    println(showRaw(value))
    println(s"::::::::::::::::::::")
    println(s"XXXXXXXXXXXXXXXXX")
    println(show(value))
    println(show(value))
    println(show(value))
    println(show(value))
    println(show(value))
    println(show(value))
    println(s"XXXXXXXXXXXXXXXXX")

    value.tree match {
      case Select(Ident(TermName("data")), TermName("underlyingPrice")) =>
        println("MAONOLO")
        println("MAONOLO")
        println("MAONOLO")
        println("MAONOLO")
        println("MAONOLO")
        c.Expr[String](Literal(Constant("underlyingPrice")))
      case value =>
        println("PEPEP" + value.toString())
        c.Expr[String](Literal(Constant("underlyingPrice")))
    }


  }






}
