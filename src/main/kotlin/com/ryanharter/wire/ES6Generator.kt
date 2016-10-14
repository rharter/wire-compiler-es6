package com.ryanharter.wire

import com.ryanharter.wire.es6emitter.*
import com.squareup.wire.schema.*
import okio.ByteString
import java.io.File
import java.nio.file.FileSystem

class ES6Generator(val schema: Schema) {

  fun generate(out: Appendable) {
    val emitter = CodeEmitter(out)

    schema.protoFiles()
        .flatMap { file ->
          file.types().map { type ->
            when (type) {
              is EnumType -> {

              }
              is MessageType -> {
                val typeClass = ClassSpec(type.type().simpleName())

                val constructor = MethodSpec("constructor")
                for (field in type.fields()) {
                  constructor.addParam(ParameterSpec(field.name(), getDefaultValue(field)))
                  constructor.addStatement(StatementSpec("this." + field.name() + " = " + field.name()))
                }
                typeClass.addMethod(constructor)

                val from = MethodSpec("from", true, mutableListOf(ParameterSpec("properties")))
                val params: MutableList<String> = mutableListOf()
                for (field in type.fields()) {
                  val name = field.name()
                  params += name
                  from.addStatement(StatementSpec("let $name = properties['$name']"))
                }
                from.addStatement(StatementSpec("return new ${typeClass.name}(${params.joinToString()})"))
                typeClass.addMethod(from)

                val toJson = MethodSpec("toJSON")
                toJson.addStatement("return {")
                for (field in params) {
                  val statement = "$field: this.$field${if (field == params.last()) "" else ","}"
                  if (field == params.first()) {
                    toJson.beginBlock(statement)
                  } else {
                    toJson.addStatement(statement)
                  }
                }
                toJson.endBlock("}")
                typeClass.addMethod(toJson)

                emitter.emit("\n").emit("module.exports.${typeClass.name} = ")
                typeClass.emit(emitter)
              }
              else -> {}
            }
          }
        }
  }

  private fun getDefaultValue(field: Field): String {
    return if (field.isRequired || !field.isOptional) {
      ""
    } else if (!field.default.isNullOrBlank()) {
      field.default
    } else {
      var string = ""
      if (field.isRepeated) {
        string += "[]"
      } else {
        when (field.type()) {
          ProtoType.STRING -> string += "\"\""
          ProtoType.BOOL -> string += "false"
          ProtoType.UINT32 -> string += "0"
          else -> string += "null"
        }
      }
      string
    }
  }

}