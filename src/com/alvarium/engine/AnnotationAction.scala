package com.alvarium.engine

import com.alvarium.annotation.SignedAnnotationBundle

import scala.concurrent.Future

class AnnotationAction(annotate: Future[SignedAnnotationBundle], publish: () => Future[Unit]) {

  def send(): Future[Unit] = publish()

  def get(): Future[SignedAnnotationBundle] = annotate
}

