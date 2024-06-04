package com.alvarium.config

import com.alvarium.annotation.SignedAnnotationBundle
import com.alvarium.engine.RegisteredChecker
import com.alvarium.serialisation.AnnotationBundleSerializer
import com.alvarium.crypto.{Hasher, Signer}
import com.alvarium.stream.DataStream

import java.util.concurrent.Executor

case class EngineConfig(
                         checkers: Array[RegisteredChecker],
                         stream: DataStream,
                         executor: Executor,
                         bundleSerializer: AnnotationBundleSerializer,
                         signer: Signer,
                         hasher: Hasher
                       ) {
}
