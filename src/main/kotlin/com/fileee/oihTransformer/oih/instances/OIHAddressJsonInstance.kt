package com.fileee.oihTransformer.oih.instances

import arrow.Kind
import arrow.core.*
import arrow.instances.EitherApplicativeInstance
import com.fileee.oihTransformer.oih.OIHAddress
import com.fileee.oihTransformer.oih.OIHAddress.Companion.city
import com.fileee.oihTransformer.oih.OIHAddress.Companion.country
import com.fileee.oihTransformer.oih.OIHAddress.Companion.countryCode
import com.fileee.oihTransformer.oih.OIHAddress.Companion.description
import com.fileee.oihTransformer.oih.OIHAddress.Companion.district
import com.fileee.oihTransformer.oih.OIHAddress.Companion.primaryContact
import com.fileee.oihTransformer.oih.OIHAddress.Companion.region
import com.fileee.oihTransformer.oih.OIHAddress.Companion.street
import com.fileee.oihTransformer.oih.OIHAddress.Companion.streetNumber
import com.fileee.oihTransformer.oih.OIHAddress.Companion.unit
import com.fileee.oihTransformer.oih.OIHAddress.Companion.zipCode
import com.fileee.oihTransformer.typeclasses.FromJson
import com.fileee.oihTransformer.typeclasses.ToJson
import com.fileee.oihTransformer.utils.JsonBuilder
import com.fileee.oihTransformer.utils.ParseError
import com.fileee.oihTransformer.utils.optional
import com.fileee.oihTransformer.utils.tryGetString
import javax.json.JsonObject

object OIHAddressToJsonInstance : ToJson<ForId, OIHAddress> {
    override fun OIHAddress.toJson(): Kind<ForId, JsonObject> = Id.just(
            convertJson(this)
    )

    fun convertJson(addr: OIHAddress): JsonObject = JsonBuilder {
        street { addr.street.orNull() }
        streetNumber { addr.streetNumber.orNull() }
        city { addr.city.orNull() }
        country { addr.country.orNull() }
        countryCode { addr.countryCode.orNull() }
        district { addr.district.orNull() }
        description { addr.description.orNull() }
        zipCode { addr.zipCode.orNull() }
        unit { addr.unit.orNull() }
        region { addr.region.orNull() }
        primaryContact { addr.primaryContact.orNull() }
    }.build()
}

fun OIHAddress.Companion.toJson() = OIHAddressToJsonInstance

object OIHAddressFromJsonInstance : FromJson<EitherPartialOf<ParseError>, OIHAddress> {
    override fun JsonObject.fromJson(): Kind<EitherPartialOf<ParseError>, OIHAddress> = Either.applicative<ParseError>().map(
            tryGetString(street).optional(),
            tryGetString(streetNumber).optional(),
            tryGetString(city).optional(),
            tryGetString(country).optional(),
            tryGetString(countryCode).optional(),
            tryGetString(district).optional(),
            tryGetString(description).optional(),
            tryGetString(zipCode).optional(),
            tryGetString(unit).optional(),
            Either.applicative<ParseError>().map(
                    tryGetString(region).optional(),
                    tryGetString(primaryContact).optional()
            ) { it }
    ) { (street, streetNumber, city, country, countryCode, district, description, zipCode, unit, regionAndPrimary) ->
        OIHAddress(
                street = street,
                streetNumber = streetNumber,
                city = city,
                country = country,
                countryCode = countryCode,
                description = description,
                district = district,
                zipCode = zipCode,
                unit = unit,
                region = regionAndPrimary.a,
                primaryContact = regionAndPrimary.b
        )
    }

}

fun OIHAddress.Companion.fromJson() = OIHAddressFromJsonInstance