package com.alvarium.checker

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.{ClassTag, classTag}

class CheckerPropsBounds[C: ClassTag](val name: Option[String]) {
  val checkerType = classTag[C].runtimeClass
}

object Checker {
  def apply[C: ClassTag] = new CheckerPropsBounds[C](None)

  def apply[C: ClassTag](name: String) = new CheckerPropsBounds[C](Some(name))
}

trait EnvironmentChecker[-P <: CheckerProps] {

  def test(props: P)(using ExecutionContext): Future[Boolean]

}
