package com.alvarium.engine

import com.alvarium.annotation.SignedAnnotationBundle

import scala.concurrent.{ExecutionContext, Future}

class AnnotationAction(annotateFuture: Future[SignedAnnotationBundle], publishFuture: () => Future[Unit])(using ExecutionContext) {

  def send(): Future[Unit] = publishFuture()

  def get(): Future[SignedAnnotationBundle] = annotateFuture
}

