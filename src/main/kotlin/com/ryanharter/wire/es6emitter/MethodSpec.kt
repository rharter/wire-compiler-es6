package com.ryanharter.wire.es6emitter

import java.io.OutputStream

class MethodSpec(val name: String,
                 val static: Boolean = false,
                 val params: MutableList<ParameterSpec> = mutableListOf(),
                 val body: MutableList<StatementSpec> = mutableListOf()) : Emitter {

  fun addParam(param: ParameterSpec): MethodSpec {
    params += param
    return this
  }

  fun beginBlock(statement: String): MethodSpec {
    body += StatementSpec(statement, 1)
    return this
  }

  fun endBlock(statement: String): MethodSpec {
    body += StatementSpec(statement, -1)
    return this
  }

  fun addStatement(statement: String): MethodSpec {
    body += StatementSpec(statement)
    return this
  }

  fun addStatement(statement: StatementSpec): MethodSpec {
    body += statement
    return this
  }

  override fun emit(out: CodeEmitter) {
    if (static) {
      out.emit("static ")
      out.append(name)
    } else {
      out.emit(name)
    }
    out.append("(")

    // params
    if (params.isNotEmpty()) {
      out.indent += 2
      for (param in params) {
        out.append(param.name)
        if (param.default.isNotBlank()) {
          out.append(" = ")
          out.append(param.default)
        }
        if (param != params.last()) {
          out.append(", ")
        }
      }
      out.indent -= 2
    }

    out.append(") {\n")

    // body
    out.indent++
    for (statement in body) {
      statement.emit(out)
      out.append("\n")
    }
    out.indent--

    out.emit("}\n")
  }

}