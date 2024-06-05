package com.alvarium

import com.alvarium.config.StreamType.Function
import com.alvarium.config.{SigningType, StreamType}
import com.alvarium.crypto.SigningKey
import com.alvarium.engine.{AlvariumActionKind, AlvariumEngineBuilder}
import com.google.crypto.tink.subtle.Ed25519Sign
import org.scalatest.flatspec.AnyFlatSpec
import zio.*

import java.util.concurrent.Executors
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}

class EngineSpec extends AnyFlatSpec {
  "Monothreaded Engine" should "not be deadlocked" in {
    val key = SigningKey.Bytes(Ed25519Sign.KeyPair.newKeyPair().getPrivateKey)
    val engine = new AlvariumEngineBuilder {
      override val signer: SigningType = SigningType.Ed25519(key)
    }.build()

    Await.result(engine.annotate(AlvariumActionKind.Custom("test"), Array(1, 2, 3)).send(), 5.seconds)
  }

  "Send " should " work " in {
    val key = SigningKey.Bytes(Ed25519Sign.KeyPair.newKeyPair().getPrivateKey)
    var sent = false
    val engine = new AlvariumEngineBuilder {
      override val signer: SigningType = SigningType.Ed25519(key)
      override val stream: StreamType = StreamType.Function(_ => Future {
        sent = true
      })
    }.build()

    Await.result(engine.annotate(AlvariumActionKind.Custom("test"), Array(1, 2, 3)).send(), 5.seconds)
    assert(sent)
  }

  "Get + send " should " not duplicate creation " in {
    val key = SigningKey.Bytes(Ed25519Sign.KeyPair.newKeyPair().getPrivateKey)
    var signAmount = 0
    var sent = false

    val engine = new AlvariumEngineBuilder {
      override val signer: SigningType = SigningType.Custom(i => {
        signAmount += 1
        new String(i)
      })
      override val stream: StreamType = StreamType.Function(_ => Future {
        sent = true
      })
    }.build()

    val action = engine.annotate(AlvariumActionKind.Custom("test"), Array(1, 2, 3))
    Await.result(action.get(), 5.seconds)
    Await.result(action.send(), 5.seconds)

    assert(sent)
    assert(signAmount == 1)
  }


  "This test using Future.sequence " should "works" in {
    given ExecutionContext = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())

    Future {
      println("This future is asynchroneous")
    }

    Future {

      val f1 = Future {
        println("running task 1")
        Thread.sleep(1500);
        println(s"1500 finished ${Thread.currentThread()}")
      }
      val f2 = Future {
        Thread.sleep(500);
        println("500 finished")
      }

      val f3 = Future {
        Thread.sleep(1000);
        println("1000 finished")
      }
      val f4 = Future {
        Thread.sleep(2000);
        println("2000 finished")
      }

      println("A")
      Await.result(f1, 5.seconds)
      println("B")
      Await.result(f2, 5.seconds)
      println("C")
      Await.result(f3, 5.seconds)
      println("D")
      Await.result(f4, 5.seconds)

      println("Finished !")

    }

    Thread.sleep(100000)
  }
}
