package com.ryanharter.wire.es6emitter

class CodeEmitter(val out: Appendable) {

  private val INDENT = "  "

  var indent = 0

  fun emit(code: String): CodeEmitter {
    out.append(INDENT.repeat(indent), code)
    return this
  }

  fun emitLn(code: String): CodeEmitter {
    out.append(INDENT.repeat(indent), code, "\n")
    return this
  }

  fun append(code: String): CodeEmitter {
    out.append(code)
    return this
  }

}