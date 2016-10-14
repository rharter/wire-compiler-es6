package com.ryanharter.wire.es6emitter

import java.io.OutputStream

class StatementSpec(val statement: String, val preIndent: Int = 0, val postIndent: Int = 0) : Emitter {
  override fun emit(out: CodeEmitter) {
    out.indent += preIndent
    out.emit(statement)
    out.indent += postIndent
  }
}