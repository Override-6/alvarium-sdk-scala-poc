package com.alvarium.checker.builtin

import com.alvarium.checker.{EnvironmentChecker, NoProps}
import com.alvarium.config.SigningType
import com.alvarium.engine.AlvariumEngineBuilder
import zio.{Task, ZIO}

import scala.concurrent.{ExecutionContext, Future}

class TestChecker extends EnvironmentChecker[NoProps] {

  override def test(props: NoProps): Task[Boolean] = ZIO.attempt {
    true
  }
}

val x = new AlvariumEngineBuilder {
  override val signer: SigningType = null

  addCheck(BuiltinChecks.Test(), new TestChecker())
}