package com.alvarium.engine

import com.alvarium.annotation.{Annotation, AnnotationBundle, SignedAnnotationBundle}
import com.alvarium.checker.*
import com.alvarium.config.EngineConfig
import zio.*

import java.time.ZonedDateTime
import scala.language.postfixOps


case class RegisteredChecker(checker: EnvironmentChecker[? <: CheckerProps], alias: Option[String])

class DefaultAlvariumEngine private(config: EngineConfig) extends AlvariumEngine {

  import config.*


  override def annotate(actionKind: AlvariumActionKind, data: Array[Byte])(props: PropsSupply[? <: CheckerProps]*): CancelableFuture[SignedAnnotationBundle] = {
    Unsafe.unsafe(implicit unsafe => {
      runToFuture(for {
        annotations <- createAnnotations(actionKind, data, props)
        serialized = bundleSerializer.serialize(annotations)
        _ <- ZIO.fromFuture(implicit ec => stream.send(serialized))
      } yield annotations)
    })
  }

  override def close(): Unit = stream.close()

  private def createAnnotations(actionKind: AlvariumActionKind, data: Array[Byte], props: Seq[PropsSupply[? <: CheckerProps]]) = {
    val annotationsFuture = ZIO.foreachPar(checkers) {
      case RegisteredChecker(checker, alias) =>
        val checkerProps = props.find {
            case PropsSupply(bounds, props) => bounds.name == alias && bounds.checkerType.isAssignableFrom(checker.getClass)
          }
          .map(_._2)
          .getOrElse(NoProps)

        val checkerCasted = checker.asInstanceOf[EnvironmentChecker[checkerProps.type]]
        checkerCasted.test(checkerProps).map(v => Annotation(checker.kind, v))
    }
    val hashFuture = ZIO.attempt {
      new String(hasher.digest(data))
    }

    for {
      annotationsFiber <- annotationsFuture.fork
      hash <- hashFuture
      annotations <- annotationsFiber.join
    } yield signBundle(AnnotationBundle(
      actionKind,
      annotations,
      ZonedDateTime.now(),
      hash
    ))
  }

  private def signBundle(bundle: AnnotationBundle): SignedAnnotationBundle = {
    SignedAnnotationBundle(bundle, config.signer.sign(bundle.identityString.getBytes))
  }

}

object DefaultAlvariumEngine {
  def apply(config: EngineConfig) = {

    val checkers = config.checkers

    val nonAliased = checkers.filter(_.alias.isEmpty)
    if (nonAliased.length != nonAliased.distinctBy(_.checker.kind).length) {
      throw new IllegalArgumentException("Configuration contains multiple checkers of the same kind with no aliases. Please provide aliases for checkers of the same type.")
    }

    if (checkers.distinctBy(r => (r.alias, r.checker.kind)).length != checkers.length) {
      throw new IllegalArgumentException("Configuration contains two checkers of the same type and of the same label")
    }

    new DefaultAlvariumEngine(config)
  }
}

private val runtime = Unsafe.unsafe { implicit unsafe =>
  Runtime.unsafe.fromLayer(Runtime.enableLoomBasedExecutor ++ Runtime.disableFlags(RuntimeFlag.FiberRoots))
}

private inline def runToFuture[E <: Throwable, A](inline io: ZIO[Unit, E, A]): CancelableFuture[A] = Unsafe.unsafe { implicit unsafe =>
  runtime
    .unsafe
    .runToFuture(io)
}

