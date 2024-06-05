package com.alvarium

import com.alvarium.config.StreamType.Function
import com.alvarium.config.{SigningType, StreamType}
import com.alvarium.crypto.SigningKey
import com.alvarium.engine.{AlvariumActionKind, AlvariumEngineBuilder}
import com.google.crypto.tink.subtle.Ed25519Sign
import org.scalatest.flatspec.AnyFlatSpec

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}

class EngineSpec extends AnyFlatSpec {
  "Send " should " work " in {
    val key = SigningKey.Bytes(Ed25519Sign.KeyPair.newKeyPair().getPrivateKey)
    var sent = false
    val engine = new AlvariumEngineBuilder {
      override val signer: SigningType = SigningType.Ed25519(key)
      override val stream: StreamType = StreamType.Function(_ => Future {
        sent = true
      })
    }.build()

    Await.result(engine.annotate(AlvariumActionKind.Custom("test"), Array(1, 2, 3)), 5.seconds)
    assert(sent)
  }
}
