package com.alvarium.annotation

import com.alvarium.engine.AlvariumActionKind
import java.time.ZonedDateTime

case class AnnotationBundle(
                             actionKind: AlvariumActionKind,
                             annotations: Iterable[Annotation],
                             timestamp: ZonedDateTime,
                             dataHash: String,
                           ) {
  def identityString = s"$actionKind$timestamp$dataHash${annotations.map(_.identityString).mkString}"
}
