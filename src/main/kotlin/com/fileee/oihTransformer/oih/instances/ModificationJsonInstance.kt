package com.fileee.oihTransformer.oih.instances

import arrow.Kind
import arrow.core.*
import com.fileee.oihTransformer.oih.Modification
import com.fileee.oihTransformer.oih.Modification.Companion.timestamp
import com.fileee.oihTransformer.oih.Modification.Companion.type
import com.fileee.oihTransformer.oih.Modification.Companion.userId
import com.fileee.oihTransformer.typeclasses.FromJson
import com.fileee.oihTransformer.typeclasses.ToJson
import com.fileee.oihTransformer.utils.JsonBuilder
import com.fileee.oihTransformer.utils.ParseError
import com.fileee.oihTransformer.utils.tryGetString
import javax.json.JsonObject

object ModificationToJsonInstance: ToJson<ForId, Modification> {
    override fun Modification.toJson(): Kind<ForId, JsonObject> = Id.just(
            convertJson(this)
    )

    fun convertJson(mod: Modification): JsonObject = JsonBuilder {
        userId { mod.userId }
        timestamp { mod.timestamp }
        type { mod.type }
    }.build()
}

fun Modification.Companion.toJson() = ModificationToJsonInstance

object ModificationFromJsonInstance: FromJson<EitherPartialOf<ParseError>, Modification> {
    override fun JsonObject.fromJson(): Kind<EitherPartialOf<ParseError>, Modification> = Either.applicative<ParseError>().map(
            tryGetString(userId),
            tryGetString(timestamp),
            tryGetString(type)
    ) { (userId, timestamp, type) -> Modification(
            userId = userId,
            timestamp = timestamp,
            type = type
    ) }
}

fun Modification.Companion.fromJson() = ModificationFromJsonInstance