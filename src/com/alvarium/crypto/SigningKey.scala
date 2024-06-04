package com.alvarium.crypto

import com.alvarium.crypto
import com.alvarium.utils.hexToBytes

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}

enum SigningKey {

  case FileKey(path: Path)
  case Bytes(key: Array[Byte])


  def content: Array[Byte] = this match
    case SigningKey.Bytes(key) => key
    case SigningKey.FileKey(path) =>
      val keyContent = Files.readString(path, StandardCharsets.US_ASCII)
      hexToBytes(keyContent).take(32)

}


