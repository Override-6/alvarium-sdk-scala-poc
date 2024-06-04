package com.alvarium.engine

import com.alvarium.checker.{CheckType, CheckerProps, EnvironmentChecker}
import com.alvarium.config.*
import com.alvarium.serialisation.{AnnotationBundleSerializer, JsoniterSerializer}
import com.alvarium.signing.{Ed25519Signer, Signer}
import com.alvarium.stream.{DataStream, MosquittoDataStream}

import java.util.concurrent.{Executor, Executors}
import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}

abstract class AlvariumEngineBuilder {
  val stream: StreamType = StreamType.None
  val executor: Executor = Executors.newVirtualThreadPerTaskExecutor()
  val serializer: SerializerType = SerializerType.Jsoniter()
  val signer: SigningType


  private val checkers = ListBuffer.empty[RegisteredChecker]

  final def addCheck(tpe: CheckType, checker: EnvironmentChecker[? <: CheckerProps], alias: Option[String] = None) = {
    checkers += RegisteredChecker(tpe, checker, alias)
  }


  final def build(): AlvariumEngine = {
    given ec: ExecutionContext = ExecutionContext.fromExecutor(executor)

    def anonymousStream(f: Array[Byte] => Future[Unit]): DataStream = new DataStream {
      override def send(data: Array[Byte]): Future[Unit] = f(data)

      override def close(): Unit = ()
    }

    val engineStream = stream match
      case cfg: StreamType.Mqtt => MosquittoDataStream(cfg)
      case StreamType.Custom(stream) => stream
      case StreamType.StreamFunction(f) => anonymousStream(f)
      case StreamType.None => anonymousStream(_ => Future.unit)

    val engineSerializer: AnnotationBundleSerializer = serializer match
      case SerializerType.Jsoniter() => new JsoniterSerializer
      case SerializerType.Custom(serializer) => serializer

    val engineSigner = signer match
      case SigningType.Ed25519(privateKey, keepSignerInMemory) => new Ed25519Signer(privateKey, keepSignerInMemory)

    val config = EngineConfig(
      checkers.toArray,
      engineStream,
      executor,
      engineSerializer,
      engineSigner
    )

    new DefaultAlvariumEngine(config)
  }
}

