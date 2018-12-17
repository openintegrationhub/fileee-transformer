package com.fileee.oihTransformer.utils

import arrow.core.*
import arrow.data.Nel
import arrow.data.k
import arrow.data.sequence
import arrow.typeclasses.binding
import com.fileee.oihTransformer.fileee.Address
import javax.json.*

class JsonBuilder(init: JsonBuilder.() -> Unit) {
    private val builder = Json.createObjectBuilder()!!

    init {
        init()
    }

    operator fun String.invoke(f: () -> Any?) {
        val v = f()
        when (v) {
            null -> return
            is String -> builder.add(this, v)
            is Long -> builder.add(this, v)
            is Boolean -> builder.add(this, v)
            is Int -> builder.add(this, v)
            is Double -> builder.add(this, v)
            is JsonValue -> builder.add(this, v)
            else -> throw InvalidTypeError("String|Int|Double|JsonValue", v::class.java.toString())
        }
    }

    fun build() = builder.build()!!
}

fun JsonObject.tryGetString(name: String): Either<ParseError, String> {
    val v = this[name]
    return when (v) {
        is JsonString -> v.string.right()
        null -> MissingRequiredValue(name).left()
        else -> InvalidTypeError("JsonString", v.valueType.toString()).left()
    }
}

fun JsonObject.tryGetJsonArray(name: String): Either<ParseError, JsonArray> {
    val v = this[name]
    return when (v) {
        is JsonArray -> v.right()
        null -> MissingRequiredValue(name).left()
        else -> InvalidTypeError("JsonArray", v.valueType.toString()).left()
    }
}

fun JsonObject.tryGetJsonObject(name: String): Either<ParseError, JsonObject> {
    val v = this[name]
    return when (v) {
        is JsonObject -> v.right()
        null -> MissingRequiredValue(name).left()
        else -> InvalidTypeError("JsonObject", v.valueType.toString()).left()
    }
}

fun <A>Iterable<Either<ParseError, A>>.foldErrors() = toList().k().sequence(Either.applicative()).fix()
        .map { Nel.fromList(it) }

fun JsonValue.asObject() = when (this) {
    is JsonObject -> this.right()
    else -> InvalidTypeError("JsonObject", this.valueType.toString()).left()
}

fun <A>Either<ParseError, A>.optional() = fold({ none<A>() }, { it.some() }).right()