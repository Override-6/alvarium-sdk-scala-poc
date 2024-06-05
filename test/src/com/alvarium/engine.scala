package com.alvarium

import com.alvarium.checker.builtin.TestChecker
import com.alvarium.checker.*
import com.alvarium.config.StreamType.Function
import com.alvarium.config.{SigningType, StreamType}
import com.alvarium.crypto.SigningKey
import com.alvarium.engine.{AlvariumActionKind, AlvariumEngineBuilder}
import com.google.crypto.tink.subtle.Ed25519Sign
import org.scalatest.flatspec.AnyFlatSpec
import zio.Task

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}

val Key = SigningKey.Bytes(Ed25519Sign.KeyPair.newKeyPair().getPrivateKey)


class EngineSpec extends AnyFlatSpec {
  "Send " should " work " in {
    var sent = false
    val engine = new AlvariumEngineBuilder {
      override val signer: SigningType = SigningType.Ed25519(Key)
      override val stream: StreamType = StreamType.Function(_ => Future {
        sent = true
      })
    }.build()

    Await.result(engine.annotate(AlvariumActionKind.Custom("test"), Array[Byte](1, 2, 3)), 5.seconds)
    assert(sent)
  }

  "Two checkers of same type with no label " should "throw " in {
    val builder = new AlvariumEngineBuilder {
      override val signer: SigningType = SigningType.Ed25519(Key)
      addCheck(new TestChecker)
      addCheck(new TestChecker)
    }

    assertThrows[IllegalArgumentException] {
      builder.build()
    }
  }

  "Two checkers of same type with same label " should "throw " in {
    val builder = new AlvariumEngineBuilder {
      override val signer: SigningType = SigningType.Ed25519(Key)
      addCheck(new TestChecker, "test")
      addCheck(new TestChecker, "test")
    }

    assertThrows[IllegalArgumentException] {
      builder.build()
    }
  }

  "Two checkers of same type with different label" should "pass" in {
    val builder = new AlvariumEngineBuilder {
      override val signer: SigningType = SigningType.Ed25519(Key)
      addCheck(new TestChecker, "test")
      addCheck(new TestChecker, "test2")
    }

    builder.build()
  }

  "Two checkers of different type with same label" should "pass" in {
    val builder = new AlvariumEngineBuilder {
      override val signer: SigningType = SigningType.Ed25519(Key)
      addCheck(new TestChecker, "test")
      addCheck(new EnvironmentChecker[NoProps] {
        override val kind: CheckType = Named("Custom")

        override def test(props: NoProps): Task[Boolean] = null
      }, "test")
    }

    builder.build()
  }


  "Custom props missing" should "throw" in {

    case class CustomProps(msg: String) extends CheckerProps

    val engine = new AlvariumEngineBuilder {
      override val signer: SigningType = SigningType.Ed25519(Key)
      addCheck(new TestChecker, "test")
      addCheck(new EnvironmentChecker[CustomProps] {
        override val kind: CheckType = Named("Custom")

        override def test(props: CustomProps): Task[Boolean] = null
      }, "test")
    }.build()

//    val future = engine.annotate(AlvariumActionKind.Custom("test"), Array[Byte](1, 2, 3))(
//      Checker[EnvironmentChecker[CustomProps]] --> CustomProps("Hello")
//    )
    Checker[EnvironmentChecker[CustomProps]]
//    Await.result(future, 5.seconds)

  }


}
