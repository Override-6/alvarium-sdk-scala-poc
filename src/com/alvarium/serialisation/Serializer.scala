package com.alvarium.serialisation

import com.alvarium.annotation.SignedAnnotationBundle

trait AnnotationBundleSerializer {
  
  def serialize(bundle: SignedAnnotationBundle): Array[Byte]


}
