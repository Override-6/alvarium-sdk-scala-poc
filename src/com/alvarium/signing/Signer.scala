package com.alvarium.signing

trait Signer {

  def sign(str: Array[Byte]): String

}
