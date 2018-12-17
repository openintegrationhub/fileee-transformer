package com.fileee.oihTransformer.oih.instances

import arrow.Kind
import arrow.core.*
import com.fileee.oihTransformer.oih.OIHContactData
import com.fileee.oihTransformer.oih.OIHContactData.Companion.description
import com.fileee.oihTransformer.oih.OIHContactData.Companion.type
import com.fileee.oihTransformer.oih.OIHContactData.Companion.value
import com.fileee.oihTransformer.typeclasses.FromJson
import com.fileee.oihTransformer.typeclasses.ToJson
import com.fileee.oihTransformer.utils.JsonBuilder
import com.fileee.oihTransformer.utils.ParseError
import com.fileee.oihTransformer.utils.optional
import com.fileee.oihTransformer.utils.tryGetString
import javax.json.JsonObject

object OIHContactDataToJsonInstance: ToJson<ForId, OIHContactData> {
    override fun OIHContactData.toJson(): Kind<ForId, JsonObject> = Id.just(
            convertJson(this)
    )

    fun convertJson(contactData: OIHContactData): JsonObject = JsonBuilder {
        value { contactData.value }
        type { contactData.type }
        description { contactData.description.orNull() }
    }.build()
}

fun OIHContactData.Companion.toJson() = OIHContactDataToJsonInstance

object OIHContactDataFromJsonInstance: FromJson<EitherPartialOf<ParseError>, OIHContactData> {
    override fun JsonObject.fromJson(): Kind<EitherPartialOf<ParseError>, OIHContactData> = Either.applicative<ParseError>().map(
            tryGetString(value),
            tryGetString(type),
            tryGetString(description).optional()
    ) { (value, type, description) -> OIHContactData(
            value = value,
            type = type,
            description = description
    ) }
}

fun OIHContactData.Companion.fromJson() = OIHContactDataFromJsonInstance