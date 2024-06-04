package com.alvarium.engine

import com.alvarium.annotation.{Annotation, AnnotationBundle, SignedAnnotationBundle}
import com.alvarium.checker.*
import com.alvarium.config.EngineConfig

import java.time.ZonedDateTime
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.postfixOps


case class RegisteredChecker(tpe: CheckType, checker: EnvironmentChecker[? <: CheckerProps], alias: Option[String])

class DefaultAlvariumEngine(config: EngineConfig) extends AlvariumEngine {

  import config.*

  private given ec: ExecutionContext = ExecutionContext.fromExecutor(config.executor)

  override def annotate(actionKind: AlvariumActionKind, data: Array[Byte], props: (CheckerPropsBounds[Any], CheckerProps)*): AnnotationAction = {
    val annotateFuture = Future {
      createAnnotations(actionKind, data, props)
    }
    lazy val publishFuture = annotateFuture.map(bundleSerializer.serialize).flatMap(stream.send)

    new AnnotationAction(annotateFuture, () => publishFuture)
  }

  override def close(): Unit = stream.close()

  private def createAnnotations(actionKind: AlvariumActionKind, data: Array[Byte], props: Seq[(CheckerPropsBounds[Any], CheckerProps)]): SignedAnnotationBundle = {
    val annotationsFuture = Future.traverse(checkers) {
      case RegisteredChecker(tpe, checker, alias) =>
        val checkerProps = props.find((bounds, props) => bounds.name == alias && bounds.checkerType.isAssignableFrom(checker.getClass))
          .map(_._2)
          .getOrElse(NoProps)

        val checkerCasted = checker.asInstanceOf[EnvironmentChecker[checkerProps.type]]
        checkerCasted.test(checkerProps).map(v => Annotation(tpe, v))
    }
    val hashFuture = Future {
      new String(hasher.digest(data))
    }

    val annotations = Await.result(annotationsFuture, Duration.Inf)
    val hash = Await.result(hashFuture, Duration.Inf)

    val bundle = AnnotationBundle(
      actionKind,
      annotations,
      ZonedDateTime.now(),
      hash
    )

    signBundle(bundle)
  }

  private def signBundle(bundle: AnnotationBundle): SignedAnnotationBundle = {
    SignedAnnotationBundle(bundle, config.signer.sign(bundle.identityString.getBytes))
  }

}
