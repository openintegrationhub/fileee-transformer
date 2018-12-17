package com.fileee.oihTransformer.oih.instances

import arrow.Kind
import arrow.core.*
import arrow.data.Nel
import com.fileee.oihTransformer.oih.*
import com.fileee.oihTransformer.oih.OIHContact.Companion.addresses
import com.fileee.oihTransformer.oih.OIHContact.Companion.contactData
import com.fileee.oihTransformer.oih.OIHContact.OIHOrganization.Companion.logo
import com.fileee.oihTransformer.oih.OIHContact.OIHOrganization.Companion.name
import com.fileee.oihTransformer.oih.OIHContact.OIHPerson.Companion.firstName
import com.fileee.oihTransformer.oih.OIHContact.OIHPerson.Companion.lastName
import com.fileee.oihTransformer.oih.OIHDataRecord.Companion.oihApplicationRecords
import com.fileee.oihTransformer.oih.OIHDataRecord.Companion.oihCreated
import com.fileee.oihTransformer.oih.OIHDataRecord.Companion.oihId
import com.fileee.oihTransformer.oih.OIHDataRecord.Companion.oihLastModified
import com.fileee.oihTransformer.oih.instances.ModificationToJsonInstance.toJson
import com.fileee.oihTransformer.oih.instances.OIHAddressToJsonInstance.toJson
import com.fileee.oihTransformer.oih.instances.OIHApplicationRecordToJsonInstance.toJson
import com.fileee.oihTransformer.oih.instances.OIHContactDataToJsonInstance.toJson
import com.fileee.oihTransformer.oih.instances.OIHContactToJsonInstance.toJson
import com.fileee.oihTransformer.typeclasses.FromJson
import com.fileee.oihTransformer.typeclasses.ToJson
import com.fileee.oihTransformer.utils.*
import javax.json.Json
import javax.json.JsonObject

object OIHContactToJsonInstance : ToJson<ForId, OIHContact> {
    override fun OIHContact.toJson(): Kind<ForId, JsonObject> = Id.just(
            convertJson(this)
    )

    fun convertJson(contact: OIHContact): JsonObject = JsonBuilder {
        name { if (contact is OIHContact.OIHOrganization) contact.name.orNull() else null }
        logo { if (contact is OIHContact.OIHOrganization) contact.logo.orNull() else null }
        firstName { if (contact is OIHContact.OIHPerson) contact.firstName.orNull() else null }
        lastName { if (contact is OIHContact.OIHPerson) contact.lastName.orNull() else null }
        addresses {
            contact.addresses.map {
                it.foldLeft(Json.createArrayBuilder()) { acc, addr ->
                    acc.add(OIHAddress.toJson().run { addr.toJson().value() })
                }.build()
            }.orNull()
        }
        contactData {
            contact.contactData.map {
                it.foldLeft(Json.createArrayBuilder()) { acc, contData ->
                    acc.add(OIHContactData.toJson().run { contData.toJson().value() })
                }.build()
            }.orNull()
        }
        oihId { contact.oihId.orNull() }
        oihCreated { contact.oihCreated.map { Modification.toJson().run { it.toJson() } }.orNull() }
        oihLastModified { contact.oihLastModified.map { Modification.toJson().run { it.toJson() } }.orNull() }
        oihApplicationRecords {
            contact.oihApplicationRecords.foldLeft(Json.createArrayBuilder()) { acc, appRed ->
                acc.add(OIHApplicationRecord.toJson().run { appRed.toJson() }.value())
            }.build()
        }
    }.build()
}

fun OIHContact.Companion.toJson() = OIHContactToJsonInstance

object OIHContactFromJsonInstance : FromJson<EitherPartialOf<ParseError>, OIHContact> {
    override fun JsonObject.fromJson(): Kind<EitherPartialOf<ParseError>, OIHContact> = Either.applicative<ParseError>().map(
            tryGetString(name).optional(),
            tryGetString(logo).optional(),
            tryGetString(firstName).optional(),
            tryGetString(lastName).optional(),
            tryGetAddresses(addresses).optional(),
            tryGetContactData(contactData).optional(),
            tryGetString(oihId).optional(),
            tryGetModification(oihCreated).optional(),
            tryGetModification(oihLastModified).optional(),
            tryGetApplicationRecords(oihApplicationRecords)
    ) { (name, logo, firstName, lastName, addresses, contactData, oihId, oihCreated, oihLastModified, oihApplicationRecords) ->
        when (name) {
            is Some -> OIHContact.OIHOrganization(
                    name = name,
                    logo = logo,
                    addresses = addresses.flatMap { it },
                    contactData = contactData.flatMap { it },
                    oihId = oihId,
                    oihCreated = oihCreated,
                    oihLastModified = oihLastModified,
                    oihApplicationRecords = oihApplicationRecords
            )
            else -> OIHContact.OIHPerson(
                    firstName = firstName,
                    lastName = lastName,
                    addresses = addresses.flatMap { it },
                    contactData = contactData.flatMap { it },
                    oihId = oihId,
                    oihCreated = oihCreated,
                    oihLastModified = oihLastModified,
                    oihApplicationRecords = oihApplicationRecords
            )
        }
    }

    private fun JsonObject.tryGetAddresses(name: String): Either<ParseError, Option<Nel<OIHAddress>>> = tryGetJsonArray(name).flatMap {
        it.map { it.asObject().flatMap { OIHAddress.fromJson().run { it.fromJson() }.fix() } }.foldErrors()
    }

    private fun JsonObject.tryGetContactData(name: String): Either<ParseError, Option<Nel<OIHContactData>>> = tryGetJsonArray(name).flatMap {
        it.map { it.asObject().flatMap { OIHContactData.fromJson().run { it.fromJson() }.fix() } }.foldErrors()
    }

    private fun JsonObject.tryGetApplicationRecords(name: String): Either<ParseError, Nel<OIHApplicationRecord>> = tryGetJsonArray(name).flatMap {
        it.map { it.asObject().flatMap { OIHApplicationRecord.fromJson().run { it.fromJson() }.fix() } }.foldErrors().flatMap {
            it.fold({ MissingRequiredValue(name).left() }, { it.right() })
        }
    }
}

fun JsonObject.tryGetModification(name: String): Either<ParseError, Modification> {
    val v = this[name]
    return v?.asObject()?.flatMap { Modification.fromJson().run { it.fromJson().fix() } }
            ?: MissingRequiredValue(name).left()
}

fun OIHContact.Companion.fromJson() = OIHContactFromJsonInstance