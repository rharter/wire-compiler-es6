package com.ryanharter.wire.es6emitter

import java.io.OutputStream

interface Emitter {
  fun emit(out: CodeEmitter)
}