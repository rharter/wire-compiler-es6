package com.ryanharter.wire

import com.ryanharter.wire.es6emitter.*
import com.squareup.wire.schema.*
import okio.ByteString
import java.io.File
import java.nio.file.FileSystem

class ES6Generator(val schema: Schema) {

  val typeMap = mapOf(
      Pair(ProtoType.BOOL, "boolean"),
      Pair(ProtoType.BYTES, "object"),
      Pair(ProtoType.DOUBLE, "number"),
      Pair(ProtoType.FLOAT, "number"),
      Pair(ProtoType.FIXED32, "number"),
      Pair(ProtoType.FIXED64, "number"),
      Pair(ProtoType.INT32, "number"),
      Pair(ProtoType.INT64, "number"),
      Pair(ProtoType.SFIXED32, "number"),
      Pair(ProtoType.SFIXED64, "number"),
      Pair(ProtoType.SINT32, "number"),
      Pair(ProtoType.SINT64, "number"),
      Pair(ProtoType.STRING, "string"),
      Pair(ProtoType.UINT32, "number"),
      Pair(ProtoType.UINT64, "number")
  )

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

                val validate = MethodSpec("validate")
                for (field in type.fields()) {
                  val name = field.name()
                  if (field.isRepeated) {
                    validate.beginBlock("if (!Array.isArray(this.$name) && (typeof this.$name) !== 'undefined') {")
                    validate.addStatement("throw Error('$name must be an array or undefined, but was ' + (typeof this.$name))")
                    validate.endBlock("}")
                  } else {
                    val jsType = typeMap[field.type()]
                    validate.beginBlock("if ((typeof this.$name) !== '$jsType' && (typeof this.$name) !== 'undefined') {")
                    validate.addStatement("throw Error('$name must be a $jsType or undefined, but was ' + (typeof this.$name))")
                    validate.endBlock("}")
                  }
                }
                typeClass.addMethod(validate)

                val toJson = MethodSpec("toJSON")
                toJson.addStatement("this.validate()")
                toJson.beginBlock("return {")
                for (field in params) {
                  toJson.addStatement("$field: this.$field${if (field == params.last()) "" else ","}")
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