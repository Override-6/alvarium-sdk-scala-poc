package com.alvarium.engine

import com.alvarium.annotation.SignedAnnotationBundle
import com.alvarium.checker.{CheckerProps, CheckerPropsBounds}
import zio.CancelableFuture

trait AlvariumEngine extends AutoCloseable {
  def annotate(actionKind: AlvariumActionKind, data: Array[Byte], props: (CheckerPropsBounds[Any], CheckerProps)*): CancelableFuture[SignedAnnotationBundle]
}
