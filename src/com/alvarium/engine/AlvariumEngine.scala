package com.alvarium.engine

import com.alvarium.checker.{CheckerProps, CheckerPropsBounds}

trait AlvariumEngine extends AutoCloseable {
  def annotate(actionKind: AlvariumActionKind, data: Array[Byte], props: (CheckerPropsBounds[Any], CheckerProps)*): AnnotationAction
}
