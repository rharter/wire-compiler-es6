package com.ryanharter.wire.es6emitter

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertTrue

internal class StatementSpecTest: Spek({
  var appender: StringBuilder? = null
  var emitter: CodeEmitter? = null

  beforeEach {
    appender = StringBuilder()
    emitter = CodeEmitter(appender!!)
  }

  describe("a statement") {
    val statement = StatementSpec("foo")

    it("should emit with a semicolon") {
      statement.emit(emitter!!)

      assertTrue { appender!!.toString().endsWith(";") }
    }
  }
})