package com.alvarium.checker.builtin

import com.alvarium.checker.CheckType

enum BuiltinChecks extends CheckType {

  case Test() 

  override def name: String = this.toString

}