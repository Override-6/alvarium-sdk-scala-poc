package com.alvarium.engine

import com.alvarium.annotation.{Annotation, AnnotationBundle, SignedAnnotationBundle}
import com.alvarium.checker.*
import com.alvarium.config.EngineConfig
import zio.*

import java.time.ZonedDateTime
import scala.language.postfixOps


case class RegisteredChecker(tpe: CheckType, checker: EnvironmentChecker[? <: CheckerProps], alias: Option[String])

class DefaultAlvariumEngine(config: EngineConfig) extends AlvariumEngine {

  import config.*

  override def annotate(actionKind: AlvariumActionKind, data: Array[Byte], props: (CheckerPropsBounds[Any], CheckerProps)*): AnnotationAction = {
    Unsafe.unsafe(implicit unsafe => {

      val promise = Promise.unsafe.make[Cause[Any], SignedAnnotationBundle](FiberId.None)
      val annotate = for {
        annotations <- createAnnotations(actionKind, data, props).tapErrorCause(promise.fail)
        _ <- promise.succeed(annotations)
      } yield annotations


      val publish = for {
        annotations <- promise.await(Trace.empty)
        serialized = bundleSerializer.serialize(annotations)
        _ <- ZIO.fromFuture(implicit ec => stream.send(serialized))
      } yield ()

      new AnnotationAction(
        annotate = runToFuture(annotate),
        publish = () => runToFuture(publish.catchAllCause(ZIO.logErrorCause(_)))
      )
    })
  }

  override def close(): Unit = stream.close()

  private def createAnnotations(actionKind: AlvariumActionKind, data: Array[Byte], props: Seq[(CheckerPropsBounds[Any], CheckerProps)]) = {
    val annotationsFuture = ZIO.foreachPar(checkers) {
      case RegisteredChecker(tpe, checker, alias) =>
        val checkerProps = props.find((bounds, props) => bounds.name == alias && bounds.checkerType.isAssignableFrom(checker.getClass))
          .map(_._2)
          .getOrElse(NoProps)

        val checkerCasted = checker.asInstanceOf[EnvironmentChecker[checkerProps.type]]
        checkerCasted.test(checkerProps).map(v => Annotation(tpe, v))
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


private val runtime = Unsafe.unsafe { implicit unsafe =>
  Runtime.unsafe.fromLayer(Runtime.enableLoomBasedExecutor ++ Runtime.disableFlags(RuntimeFlag.FiberRoots))
}

private inline def runToFuture[E <: Throwable, A](inline io: ZIO[Unit, E, A]): CancelableFuture[A] = Unsafe.unsafe { implicit unsafe =>
  runtime
    .unsafe
    .runToFuture(io)
}

