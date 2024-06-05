package com.alvarium.checker.builtin

import com.alvarium.checker.{CheckType, EnvironmentChecker, NoProps}
import zio.{Task, ZIO}

class TestChecker extends EnvironmentChecker[NoProps] {

  override val kind: CheckType = BuiltinChecks.Test()

  override def test(props: NoProps): Task[Boolean] = ZIO.attempt {
    true
  }
}

