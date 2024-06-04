package com.alvarium.stream

import scala.concurrent.Future

trait DataStream extends AutoCloseable {
  def send(data: Array[Byte]): Future[Unit]
}
