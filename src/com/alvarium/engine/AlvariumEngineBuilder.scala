package com.alvarium.engine

import com.alvarium.checker.{CheckType, CheckerProps, EnvironmentChecker}
import com.alvarium.config.*
import com.alvarium.crypto.{Ed25519Signer, Hasher}
import com.alvarium.serialisation.{AnnotationBundleSerializer, JsoniterSerializer}
import com.alvarium.stream.{DataStream, MosquittoDataStream}

import java.security.MessageDigest
import java.util.concurrent.{Executor, Executors}
import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}

abstract class AlvariumEngineBuilder {
  val stream: StreamType = StreamType.None
  val executor: Executor = Executors.newVirtualThreadPerTaskExecutor()
  val serializer: SerializerType = SerializerType.Jsoniter()
  val signer: SigningType
  val hasher: HasherType = HasherType.MessageDigest("SHA-256")


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

    val engineHasher: Hasher = hasher match
      case HasherType.MessageDigest(algorithm) => (data: Array[Byte]) => {
        MessageDigest.getInstance(algorithm).digest(data)
      }
      case HasherType.Custom(hasher) => hasher

    val config = EngineConfig(
      checkers.toArray,
      engineStream,
      executor,
      engineSerializer,
      engineSigner,
      engineHasher
    )

    new DefaultAlvariumEngine(config)
  }
}

