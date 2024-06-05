

import mill._
import scalalib._

object src extends RootModule with ScalaModule {
  def scalaVersion = "3.4.0"

  override def compileIvyDeps = Agg(
    ivy"com.github.plokhotnyuk.jsoniter-scala::jsoniter-scala-macros:2.30.1"
  )

  override def ivyDeps = Agg(
    ivy"org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5",
    ivy"com.github.plokhotnyuk.jsoniter-scala::jsoniter-scala-core:2.30.1",
    ivy"com.google.crypto.tink:tink:1.13.0",
    ivy"dev.zio::zio:2.1.2",
  )

  object test extends ScalaModule with TestModule.ScalaTest {
    override def scalaVersion = "3.4.0"

    override def ivyDeps = Agg(
      ivy"org.scalatest::scalatest-flatspec:3.2.18",
      ivy"dev.zio::zio:2.1.2",
    )

    override def moduleDeps = Seq(src)
  }

}

