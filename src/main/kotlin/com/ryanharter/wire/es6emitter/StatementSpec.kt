package com.ryanharter.wire.es6emitter

import java.io.OutputStream

class StatementSpec(val statement: String, val indent: Int = 0) : Emitter {
  override fun emit(out: CodeEmitter) {
    out.indent += indent
    out.emit(statement)
  }
}