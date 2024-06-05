package com.alvarium.checker

import zio.Task

import scala.annotation.targetName
import scala.quoted.{Expr, Quotes, Type}
import scala.reflect.{ClassTag, classTag}

class CheckerPropsBounds[A <: CheckerProps](val checkerType: Class[?], val name: Option[String]) {

  @targetName("relies")
  inline def ->(props: A): PropsSupply[A] = {
    PropsSupply(this, props)
  }
}

object Checker {
  inline def apply[C <: EnvironmentChecker[?] : ClassTag] = ${ checkerPropsMacro[C]() }

  //  def apply[C <: EnvironmentChecker[A] : ClassTag](name: String) = new CheckerPropsBounds[C](Some(name))
}

import scala.quoted.*

def checkerPropsMacro[C <: EnvironmentChecker[?] : Type]()(using Quotes): Expr[Any] =
  import quotes.reflect.*
  val repr = TypeRepr.of[C]
  val fullName = repr.typeSymbol.fullName.toString

//  val propArg = repr.typeArgs.head.fullName.toString

  val expr = New(
  AppliedTypeTree(
    Ident(TypeName("CheckerPropsBounds")),
    List(Ident(TypeName("NoProps")))
  ),
  List(
    Apply(
      Select(
        Ident(TermName("Class")),
        TermName("forName")
      ),
      List(Literal(Constant("zizi")))
    ),
    Ident(TermName("None"))
  )
)

  report.error(expr.toString)

  expr

trait EnvironmentChecker[-P <: CheckerProps] {

  val kind: CheckType

  def test(props: P): Task[Boolean]

}


case class PropsSupply[P <: CheckerProps](bounds: CheckerPropsBounds[P], props: P)

