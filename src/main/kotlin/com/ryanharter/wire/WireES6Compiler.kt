package com.ryanharter.wire

import com.ryanharter.wire.es6emitter.ClassSpec
import com.ryanharter.wire.es6emitter.MethodSpec
import com.ryanharter.wire.es6emitter.ParameterSpec
import com.ryanharter.wire.es6emitter.StatementSpec
import com.squareup.wire.schema.*
import java.io.File
import java.io.OutputStreamWriter
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.util.*

fun main(args: Array<String>) {
    WireES6Compiler(args).compile()
}

class WireES6Compiler(args: Array<String>) {
    val FLAG_PROTO_PATH = "--proto-path="
    val FLAG_JS_OUT = "--js-out="
    val FLAG_FILES = "--files="

    val fs = FileSystems.getDefault()
    val protoFiles: List<String>
    val jsOut: String
    val sourceFileNames: List<String>

    init {
        val protoFiles: MutableList<String> = mutableListOf()
        var jsOut: String = "."
        val sourceFileNames: MutableList<String> = mutableListOf()

        for (arg in args) {
            when (readPrefix(arg)) {
                FLAG_PROTO_PATH -> protoFiles += arg.removePrefix(FLAG_PROTO_PATH)
                FLAG_JS_OUT -> jsOut = arg.removePrefix(FLAG_JS_OUT)
                FLAG_FILES -> sourceFileNames.addAll(File(arg.removePrefix(FLAG_FILES)).let {
                    Scanner(it, "UTF-8").useDelimiter("\\A").next().split("\n")
                })
            }
        }

        this.protoFiles = protoFiles
        this.jsOut = jsOut
        this.sourceFileNames = sourceFileNames
    }

    private fun readPrefix(s: String): String? {
        return s.substring(0, s.indexOfFirst { it == '=' } + 1)
    }

    fun compile() {
        val schema = with(SchemaLoader()) {
            protoFiles.forEach { addSource(File(it)) }
            load()
        }.prune(IdentifierSet.Builder().exclude("google.protobuf.*").build())

        val path = fs.getPath(jsOut)
        val file = path.resolve("protos.js")
        println("writing file to $file")
        OutputStreamWriter(Files.newOutputStream(file)).use {
            ES6Generator(schema).generate(it)
        }
    }

}