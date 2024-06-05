package com.alvarium.stream

import scala.concurrent.{ExecutionContext, Future}

trait DataStream extends AutoCloseable {
  def send(data: Array[Byte])(using ExecutionContext): Future[Unit]
}
