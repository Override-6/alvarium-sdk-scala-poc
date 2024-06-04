package com.alvarium.crypto

trait Signer {

  def sign(str: Array[Byte]): String

}
