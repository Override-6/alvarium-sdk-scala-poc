package com.alvarium.stream

import zio.Task

import scala.concurrent.{ExecutionContext, Future}

trait DataStream extends AutoCloseable {
  def send(data: Array[Byte])(using ExecutionContext): Future[Unit]
}
