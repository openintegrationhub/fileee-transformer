package com.fileee.oihTransformer.fileee.instances

import arrow.Kind
import arrow.core.*
import com.fileee.oihTransformer.fileee.ContactData
import com.fileee.oihTransformer.fileee.ContactData.Companion.description
import com.fileee.oihTransformer.fileee.ContactData.Companion.type
import com.fileee.oihTransformer.fileee.ContactData.Companion.value
import com.fileee.oihTransformer.typeclasses.FromJson
import com.fileee.oihTransformer.typeclasses.ToJson
import com.fileee.oihTransformer.utils.JsonBuilder
import com.fileee.oihTransformer.utils.ParseError
import com.fileee.oihTransformer.utils.optional
import com.fileee.oihTransformer.utils.tryGetString
import javax.json.JsonObject

object ContactDataToJsonInstance: ToJson<ForId, ContactData> {
    override fun ContactData.toJson(): Kind<ForId, JsonObject> = Id.just(
            convertJson(this)
    )

    fun convertJson(contactData: ContactData): JsonObject =
            JsonBuilder {
                value { contactData.value }
                type { contactData.type }
                description { contactData.description.orNull() }
            }.build()
}

fun ContactData.Companion.toJson() = ContactDataToJsonInstance

object ContactDataFromJsonInstance: FromJson<EitherPartialOf<ParseError>, ContactData> {
    override fun JsonObject.fromJson(): Kind<EitherPartialOf<ParseError>, ContactData> = Either.applicative<ParseError>().map(
            tryGetString(value),
            tryGetString(type),
            tryGetString(description).optional()
    ) { (value, type, description) ->
        ContactData(
                value = value,
                type = type,
                description = description
        )
    }
}

fun ContactData.Companion.fromJson() = ContactDataFromJsonInstance