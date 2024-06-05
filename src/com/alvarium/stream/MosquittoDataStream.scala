package com.alvarium.stream

import com.alvarium.config.{MqttCredentials, StreamType}
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence
import org.eclipse.paho.client.mqttv3.{IMqttClient, MqttAsyncClient, MqttClient, MqttConnectOptions, MqttMessage}

import java.util.concurrent.{Executor, ScheduledExecutorService}
import scala.concurrent.{ExecutionContext, Future}

private val PublishTimeoutSeconds = 2

class MosquittoDataStream(client: IMqttClient, topics: Array[String], qos: Int) extends DataStream {

  override def send(data: Array[Byte])(using ExecutionContext): Future[Unit] = Future {
    if !client.isConnected then
      client.reconnect()


    topics.foreach(client.publish(_, data, qos, false))
  }

  override def close(): Unit = client.close()
}

object MosquittoDataStream {
  def apply(cfg: StreamType.Mqtt) = {
    val options = new MqttConnectOptions()
    cfg.credentials match
      case Some(MqttCredentials(user, password)) =>
        options.setUserName(user)
        options.setPassword(password.toCharArray)
      case None =>

    options.setCleanSession(cfg.isClean)
    options.setConnectionTimeout(PublishTimeoutSeconds)

    val client = new MqttClient(cfg.endpoint.toString, cfg.clientId)
    client.connect(options)

    new MosquittoDataStream(client, cfg.topics.toArray, cfg.qos)
  }
}

