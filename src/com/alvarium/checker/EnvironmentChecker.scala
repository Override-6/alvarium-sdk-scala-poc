package com.alvarium.checker

import zio.Task

import scala.annotation.targetName
import scala.reflect.{ClassTag, classTag}

class CheckerPropsBounds[A <: CheckerProps](val checkerType: Class[?], val name: Option[String]) {

  @targetName("relies")
  inline def ->(props: A): PropsSupply[A] = {
    PropsSupply(this, props)
  }
}

object Checker {
  inline def apply[C <: EnvironmentChecker[?] : ClassTag] = new CheckerPropsBounds[CheckerProps](classTag[C].runtimeClass, None)

  inline def apply[C <: EnvironmentChecker[?] : ClassTag](name: String) = new CheckerPropsBounds[CheckerProps](classTag[C].runtimeClass, Some(name))
}


trait EnvironmentChecker[-P <: CheckerProps] {

  val kind: CheckType

  def test(props: P): Task[Boolean]

}


case class PropsSupply[P <: CheckerProps](bounds: CheckerPropsBounds[P], props: P)

