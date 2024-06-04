package com.alvarium.config

import com.alvarium.crypto.SigningKey


enum SigningType {
  case Ed25519(privateKey: SigningKey, keepSignerInMemory: Boolean = true)
}
