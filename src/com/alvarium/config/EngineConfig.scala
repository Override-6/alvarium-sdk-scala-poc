package com.alvarium.config

import com.alvarium.crypto.{Hasher, Signer}
import com.alvarium.engine.RegisteredChecker
import com.alvarium.serialisation.AnnotationBundleSerializer
import com.alvarium.stream.DataStream

case class EngineConfig(
                         checkers: Array[RegisteredChecker],
                         stream: DataStream,
                         bundleSerializer: AnnotationBundleSerializer,
                         signer: Signer,
                         hasher: Hasher
                       ) {
}
