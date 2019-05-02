package com.fileee.oihTransformer.fileee.instances

import arrow.Kind
import arrow.core.*
import arrow.data.k
import com.fileee.oihTransformer.fileee.Address
import com.fileee.oihTransformer.fileee.BrandingInfo
import com.fileee.oihTransformer.fileee.Contact
import com.fileee.oihTransformer.fileee.Contact.Companion.addresses
import com.fileee.oihTransformer.fileee.Contact.Companion.brandingInfo
import com.fileee.oihTransformer.fileee.Contact.Companion.contactData
import com.fileee.oihTransformer.fileee.Contact.Companion.logo
import com.fileee.oihTransformer.fileee.Contact.Companion.name
import com.fileee.oihTransformer.fileee.ContactData
import com.fileee.oihTransformer.fileee.Entity.Companion.id
import com.fileee.oihTransformer.typeclasses.FromJson
import com.fileee.oihTransformer.typeclasses.ToJson
import com.fileee.oihTransformer.utils.*
import javax.json.Json
import javax.json.JsonObject

object ContactToJsonInstance : ToJson<ForId, Contact> {
    override fun Contact.toJson(): Kind<ForId, JsonObject> = Id.just(
            convertJson(this)
    )

    fun convertJson(contact: Contact): JsonObject =
            JsonBuilder {
                id { contact.id.orNull() }
                name { contact.name }
                logo { contact.logo.orNull() }
                addresses { contact.addresses.map {
                    it.foldLeft(Json.createArrayBuilder()) { arr, add ->
                        arr.add(Address.toJson().run { add.toJson().value() })
                    }.build()
                }.orNull() }
                brandingInfo { contact.branding.map { BrandingInfo.toJson().run { it.toJson().value() } }.orNull() }
                contactData { contact.contactData.map {
                    it.foldLeft(Json.createArrayBuilder()) { arr, contData ->
                        arr.add(ContactData.toJson().run { contData.toJson().value() })
                    }.build()
                }.orNull() }
            }.build()
}

fun Contact.Companion.toJson() = ContactToJsonInstance

object ContactFromJsonInstance : FromJson<EitherPartialOf<ParseError>, Contact> {
    override fun JsonObject.fromJson(): Kind<EitherPartialOf<ParseError>, Contact> = Either.applicative<ParseError>().map(
            tryGetString(id).optional(),
            tryGetString(name),
            tryGetString(logo).optional(),
            tryGetAddresses(addresses).optional(),
            tryGetBrandingInfo(brandingInfo).optional(),
            tryGetContactData(contactData).optional()
    ) { (id, name, logo, addresses, brandingInfo, contactData) ->
        Contact(
                id = id,
                name = name,
                logo = logo,
                addresses = addresses.flatMap { it },
                branding = brandingInfo,
                contactData = contactData.flatMap { it }
        )
    }

    private fun JsonObject.tryGetAddresses(name: String) = tryGetJsonArray(name).flatMap {
        it.map { it.asObject().flatMap { Address.fromJson().run { it.fromJson() }.fix() } }.k().foldErrors()
    }

    private fun JsonObject.tryGetBrandingInfo(name: String) = tryGetJsonObject(name).flatMap {
        BrandingInfo.fromJson().run { it.fromJson() }.fix()
    }

    private fun JsonObject.tryGetContactData(name: String) = tryGetJsonArray(name).flatMap {
        it.map { it.asObject().flatMap { ContactData.fromJson().run { it.fromJson().fix() } } }.foldErrors()
    }
}

fun Contact.Companion.fromJson() = ContactFromJsonInstance