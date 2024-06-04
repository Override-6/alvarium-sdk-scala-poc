package com.alvarium.signing

import com.alvarium.utils.bytesToHex
import com.google.crypto.tink.PublicKeySign
import com.google.crypto.tink.subtle.Ed25519Sign

class Ed25519Signer(key: SigningKey, keepSignerInMemory: Boolean) extends Signer {

  private def newSigner: PublicKeySign = {
    val keyContent = key.content
    new Ed25519Sign(keyContent)
  }

  private val cachedSigner = if keepSignerInMemory then newSigner else null

  private def signer = if keepSignerInMemory then cachedSigner else newSigner

  override def sign(bytes: Array[Byte]): String = {
    bytesToHex(signer.sign(bytes))
  }
}
