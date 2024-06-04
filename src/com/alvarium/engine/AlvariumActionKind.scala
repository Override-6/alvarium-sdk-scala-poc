package com.alvarium.engine

enum AlvariumActionKind {
  
  case Create()
  case Publish()
  case Mutate()
  case Transit()
  
  case Custom(name: String)
  
  def getActionName = this match
    case AlvariumActionKind.Create() => "create"
    case AlvariumActionKind.Publish() => "publish"
    case AlvariumActionKind.Mutate() => "mutate"
    case AlvariumActionKind.Transit() => "transit"
    case AlvariumActionKind.Custom(name) => name

}