package com.ryanharter.wire.es6emitter

import java.io.OutputStream

class ClassSpec(val name: String, val methods: MutableList<MethodSpec> = mutableListOf()) : Emitter {

  fun addMethod(method: MethodSpec): ClassSpec {
    methods += method
    return this
  }

  override fun emit(out: CodeEmitter) {
    out.emit("class $name {\n")

    out.indent++
    for (method in methods) {
      method.emit(out)
      out.append("\n")
    }
    out.indent--

    out.emit("}\n")
  }
}