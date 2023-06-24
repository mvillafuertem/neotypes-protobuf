package io.github.mvillafuertem

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

// https://www.youtube.com/watch?v=8ryhJOE1m30
// https://www.youtube.com/watch?v=8scL8dzRqjA
// https://docs.scala-lang.org/overviews/reflection/symbols-trees-types.html
// https://docs.scala-lang.org/overviews/quasiquotes/lifting.html
// import reflect.runtime.universe._
// showRaw{ reify{ List(1, 2, 3) } }
object SimpleMacro {

  sealed trait Ast

  case class Subtract(a: Ast, b: Ast) extends Ast
  case class Concat(a: Ast, b: Ast)   extends Ast
  case class Key(value: String)       extends Ast

  case class RemoteData(
    symbol: String,
    strikePrice: Int,
    underlyingPrice: Int
  )

//  def defOddEvenMacro(number: Int): String = macro defMacroImplRef
//
//  def defMacroImplRef(
//    c: blackbox.Context
//  )(number: c.Expr[Int]): c.Expr[String] = {
//    import c.universe._
//
//    // val q"10 $op 20" = q"10 + 20"
//    val Apply(
//      Select(Literal(Constant(a: Int)), TermName(operation: String)),
//      List(Literal(Constant(b: Int)))
//    ) = q"10 + 20"
//
//    val Literal(Constant(s_number: Int)) = number.tree
//
//    val result = s_number % 2 match {
//      case 0 => Literal(Constant(s"even $a $operation $b"))
//      case _ => Literal(Constant(s"odd $a $operation $b"))
//    }
//
//    c.Expr[String](result)
//
//  }
  def query(value: Any): io.github.mvillafuertem.SimpleMacro.Ast = macro queryImp

  def queryImp(
    c: blackbox.Context
  )(value: c.Expr[Any]) = {
    import c.universe._

    println(s"::::::::::::::::::::")
    println(showRaw(value))
    println(s"::::::::::::::::::::")

    def recursive(curr: Expr[_]): Ast = curr match {
      case Expr(Apply(Select(left, TermName("$minus")), right)) =>
        println(showRaw("$minus$minus$minus$minus"))
        println(showRaw("$minus$minus$minus$minus"))
        println(showRaw("$minus$minus$minus$minus"))
        recursive(c.Expr(left))
      case Expr(Select(Ident(TermName("data")), fieldName))     =>
        Key(fieldName.decodedName.toString)
      case value                                                =>
        println(showRaw(value))
        println(showRaw(value))
        println(showRaw(value))
        println(showRaw(value))
        Key("hola")
    }

    val tree = recursive(value)

    def to(ast: Ast): c.universe.Tree = ast match {
      case Subtract(a, b) =>
        q"_root_.io.github.mvillafuertem.SimpleMacro.Subtract(${to(a)}, ${to(b)})"

      case Concat(a, b) =>
        q"_root_.io.github.mvillafuertem.SimpleMacro.Concat(${to(a)}, ${to(b)})"

      case Key(value) =>
        q"_root_.io.github.mvillafuertem.SimpleMacro.Key($value)"

    }

    to(tree)

  }

}

object Macros {

  sealed trait NonZeroNumber

  case class Positive(value: Int) extends NonZeroNumber

  case class Negative(value: Int) extends NonZeroNumber
  def nonZeroNum(number: Int): NonZeroNumber = macro nonZeroNum_impl

  def nonZeroNum_impl(
    c: blackbox.Context
  )(number: c.Expr[Int]): c.universe.Tree = {
    import c.universe._

    // unpack a static value from the method argument
    val Literal(Constant(constValue: Int)) = number.tree

    // construct a NonZeroNumber based on that value
    if (constValue > 0) {
      q"_root_.io.github.mvillafuertem.Macros.Positive($number)"
    } else if (constValue < 0) {
      q"_root_.io.github.mvillafuertem.Macros.Negative($number)"
    } else {
      c.abort(c.enclosingPosition, "Non zero value expected")
    }
  }
}
