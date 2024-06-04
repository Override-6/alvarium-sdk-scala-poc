package com.alvarium.annotation

import com.alvarium.checker.CheckType

case class Annotation(tpe: CheckType, isSatisfied: Boolean) {
  def identityString = s"$tpe$isSatisfied"
}
