package com.alvarium.config

import com.alvarium.annotation.SignedAnnotationBundle
import com.alvarium.serialisation.AnnotationBundleSerializer

enum SerializerType {
  case Jsoniter()
  case Custom(f: AnnotationBundleSerializer)
}


