package com.alvarium.crypto

trait Hasher {
  def digest(data: Array[Byte]): Array[Byte]
}
