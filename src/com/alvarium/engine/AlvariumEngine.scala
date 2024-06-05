package com.alvarium.engine

import com.alvarium.annotation.SignedAnnotationBundle
import com.alvarium.checker.CheckerProps
import zio.CancelableFuture
import com.alvarium.checker.PropsSupply

trait AlvariumEngine extends AutoCloseable {
  def annotate(actionKind: AlvariumActionKind, data: Array[Byte])(props: PropsSupply[? <: CheckerProps]*): CancelableFuture[SignedAnnotationBundle]

  def annotate(actionKind: AlvariumActionKind, data: Array[Byte]): CancelableFuture[SignedAnnotationBundle] = annotate(actionKind, data)()
}

