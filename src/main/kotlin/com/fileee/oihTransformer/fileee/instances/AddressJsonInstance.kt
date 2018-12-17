package com.fileee.oihTransformer.fileee.instances

import arrow.Kind
import arrow.core.*
import com.fileee.oihTransformer.fileee.Address
import com.fileee.oihTransformer.fileee.Address.Companion.city
import com.fileee.oihTransformer.fileee.Address.Companion.countryCode
import com.fileee.oihTransformer.fileee.Address.Companion.street
import com.fileee.oihTransformer.fileee.Address.Companion.zipCode
import com.fileee.oihTransformer.fileee.Entity.Companion.id
import com.fileee.oihTransformer.typeclasses.FromJson
import com.fileee.oihTransformer.typeclasses.ToJson
import com.fileee.oihTransformer.utils.JsonBuilder
import com.fileee.oihTransformer.utils.ParseError
import com.fileee.oihTransformer.utils.optional
import com.fileee.oihTransformer.utils.tryGetString
import javax.json.JsonObject

object AddressToJsonInstance : ToJson<ForId, Address> {
    override fun Address.toJson(): Kind<ForId, JsonObject> = Id.just(
            convertJson(this)
    )

    fun convertJson(addr: Address): JsonObject =
            JsonBuilder {
                id { addr.id.orNull() }
                street { addr.street.orNull() }
                city { addr.city.orNull() }
                countryCode { addr.countryCode.orNull() }
                zipCode { addr.zipCode.orNull() }
            }.build()
}

fun Address.Companion.toJson() = AddressToJsonInstance

object AddressFromJsonInstance : FromJson<EitherPartialOf<ParseError>, Address> {
    override fun JsonObject.fromJson(): Kind<EitherPartialOf<ParseError>, Address> = Either.applicative<ParseError>().map(
            tryGetString(id).optional(),
            tryGetString(street).optional(),
            tryGetString(countryCode).optional(),
            tryGetString(city).optional(),
            tryGetString(zipCode).optional()
    ) { (id, street, countryCode, city, zipCode) ->
        Address(
                id = id,
                street = street,
                countryCode = countryCode,
                city = city,
                zipCode = zipCode
        )
    }
}

fun Address.Companion.fromJson() = AddressFromJsonInstance