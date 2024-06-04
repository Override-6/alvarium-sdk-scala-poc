package com.alvarium.config

import com.alvarium.crypto.Hasher

enum HasherType {
  case MessageDigest(algorithm: String)
  case Custom(hasher: Hasher)
}