package com.alvarium.checker

trait CheckType {
  
  def name: String

}

case class Named(name: String) extends CheckType