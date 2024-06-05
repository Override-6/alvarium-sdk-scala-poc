package com.alvarium.config

import com.alvarium.crypto.{Signer, SigningKey}


enum SigningType {
  case Ed25519(privateKey: SigningKey, keepSignerInMemory: Boolean = true)
  case Custom(signer: Signer)
}
