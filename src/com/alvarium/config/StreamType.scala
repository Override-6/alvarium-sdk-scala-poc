package com.alvarium.config

import com.alvarium.stream.DataStream

import scala.concurrent.{ExecutionContext, Future}

enum StreamType {
  case Mqtt(endpoint: Endpoint, clientId: String, qos: Int = 2, isClean: Boolean = true, credentials: Option[MqttCredentials] = scala.None)(val topics: String*)
  case Custom(stream: DataStream)
  case Function(f: Array[Byte] => ExecutionContext ?=> Future[Unit])
  case None
}

case class MqttCredentials(user: String, password: String)

case class Endpoint(address: String, protocol: String, port: Int) {
  override def toString: String = s"$protocol://$address:$port"
}


