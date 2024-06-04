package com.alvarium.config

import com.alvarium.signing.SigningKey


enum SigningType {
  case Ed25519(privateKey: SigningKey, keepSignerInMemory: Boolean = true)
}
