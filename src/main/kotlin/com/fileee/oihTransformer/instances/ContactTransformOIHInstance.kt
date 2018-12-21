package com.fileee.oihTransformer.instances

import arrow.Kind
import arrow.core.*
import arrow.data.Nel
import arrow.data.nel
import arrow.instances.monoid
import com.fileee.oihTransformer.fileee.*
import com.fileee.oihTransformer.fileee.BrandingInfo.Companion.brandLinkColorCode
import com.fileee.oihTransformer.fileee.BrandingInfo.Companion.brandTextColorCode
import com.fileee.oihTransformer.fileee.BrandingInfo.Companion.headerBackgroundColorCode
import com.fileee.oihTransformer.fileee.BrandingInfo.Companion.headerTextColorCode
import com.fileee.oihTransformer.fileee.BrandingInfo.Companion.logoBackgroundColorCode
import com.fileee.oihTransformer.fileee.BrandingInfo.Companion.logoTextColorCode
import com.fileee.oihTransformer.fileee.Contact.Companion.name
import com.fileee.oihTransformer.oih.OIHAddress
import com.fileee.oihTransformer.oih.OIHApplicationRecord
import com.fileee.oihTransformer.oih.OIHContact
import com.fileee.oihTransformer.oih.OIHContactData
import com.fileee.oihTransformer.typeclasses.TransformOIH
import com.fileee.oihTransformer.utils.FileeeApplicationId
import com.fileee.oihTransformer.utils.HandlerException
import com.fileee.oihTransformer.utils.MissingRequiredValue

object ContactTransformOIHInstance : TransformOIH<EitherPartialOf<HandlerException>, Contact, OIHContact> {
    override fun Contact.transformToOIH(): Kind<EitherPartialOf<HandlerException>, OIHContact> =
            OIHContact.OIHOrganization(
                    name = name.some(),
                    logo = logo,
                    addresses = addresses.map { it.map { transformAddressToOIH(it) } },
                    contactData = contactData.map {
                        it.map { transformContactDataToOIH(it) } + branding.fold({ emptyList<OIHContactData>() }, { contactDataFromBrandingInfo(it) })
                    },
                    oihApplicationRecords = OIHApplicationRecord(
                            recordUid = id,
                            applicationUid = FileeeApplicationId,
                            created = none(),
                            lastModified = none(),
                            modificationHistory = none()
                    ).nel()
            ).right()

    override fun OIHContact.transformToFileee(): Kind<EitherPartialOf<HandlerException>, Contact> = when (this) {
        is OIHContact.OIHOrganization -> name
        is OIHContact.OIHPerson -> firstName.flatMap { first -> lastName.map { "$first $it" } }.orElse { lastName }
    }.toEither { MissingRequiredValue(name) }.map { name ->
        Contact(
                id = findFileeeRecordId(oihApplicationRecords),
                name = name,
                logo = if (this is OIHContact.OIHOrganization) logo else none(),
                addresses = addresses.map { it.map { transformAddressToFileee(it) } },
                contactData = contactData.flatMap { Nel.fromList(it.all.filter { isBrandingInfo(it).not() }.map { transformContactDataToFileee(it) }) },
                branding = brandingInfoFromContactData(contactData.fold({ emptyList<OIHContactData>() }, { it.all }))
        )
    }

    // TODO applicationUid: The application id is currently hardcoded to "Fileee" this may change, and afaik oih has not finalized that concept yet
    private fun findFileeeRecordId(records: Nel<OIHApplicationRecord>): Option<UUID> = records.all.find { it.applicationUid == FileeeApplicationId }.toOption()
            .flatMap { it.recordUid }

    private fun transformAddressToFileee(addr: OIHAddress): Address = Address(
            id = none(), // TODO Fileee requires an id, otherwise new contacts will be generated, however this is not yet part of the oih data model
            street = addr.street.flatMap { str -> addr.streetNumber.map { "$str $it"} },
            city = addr.city,
            countryCode = addr.countryCode,
            zipCode = addr.zipCode
    )

    private fun isBrandingInfo(contactData: OIHContactData): Boolean = when (contactData.type) {
        brandLinkColorCode, brandTextColorCode, headerBackgroundColorCode, headerTextColorCode, logoBackgroundColorCode, logoTextColorCode -> true
        else -> false
    }

    private fun transformContactDataToFileee(contactData: OIHContactData): ContactData = ContactData(
            value = contactData.value,
            type = contactData.type.toUpperCase(),
            description = contactData.description
    )

    private fun brandingInfoFromContactData(contactData: List<OIHContactData>): Option<BrandingInfo> = when (contactData.any { isBrandingInfo(it) }) {
        true -> BrandingInfo(
                brandLinkColorCode = contactData.find { it.type == brandLinkColorCode }.toOption().map { it.value },
                brandTextColorCode = contactData.find { it.type == brandTextColorCode }.toOption().map { it.value },
                headerBackgroundColorCode = contactData.find { it.type == headerBackgroundColorCode }.toOption().map { it.value },
                headerTextColorCode = contactData.find { it.type == headerTextColorCode }.toOption().map { it.value },
                logoBackgroundColorCode = contactData.find { it.type == logoBackgroundColorCode }.toOption().map { it.value },
                logoTextColorCode = contactData.find { it.type == logoTextColorCode }.toOption().map { it.value }
        ).some()
        false -> none()
    }

    private fun transformAddressToOIH(addr: Address): OIHAddress = OIHAddress(
            street = addr.street,
            streetNumber = none(),
            description = none(),
            primaryContact = none(),
            region = none(),
            unit = none(),
            zipCode = addr.zipCode,
            district = none(),
            countryCode = addr.countryCode,
            country = none(),
            city = addr.city
    )

    private fun transformContactDataToOIH(contData: ContactData): OIHContactData = OIHContactData(
            value = contData.value,
            type = contData.type.toLowerCase(),
            description = contData.description
    )

    private fun contactDataFromBrandingInfo(brand: BrandingInfo): List<OIHContactData> = brand.let { brandingInfo ->
        brandingInfo.brandLinkColorCode.fold({ emptyList<OIHContactData>() }, { listOf(OIHContactData(it, brandLinkColorCode)) }) +
                brandingInfo.brandTextColorCode.fold({ emptyList<OIHContactData>() }, { listOf(OIHContactData(it, brandTextColorCode)) }) +
                brandingInfo.headerBackgroundColorCode.fold({ emptyList<OIHContactData>() }, { listOf(OIHContactData(it, headerBackgroundColorCode)) }) +
                brandingInfo.headerTextColorCode.fold({ emptyList<OIHContactData>() }, { listOf(OIHContactData(it, headerTextColorCode)) }) +
                brandingInfo.logoBackgroundColorCode.fold({ emptyList<OIHContactData>() }, { listOf(OIHContactData(it, logoBackgroundColorCode)) }) +
                brandingInfo.logoTextColorCode.fold({ emptyList<OIHContactData>() }, { listOf(OIHContactData(it, logoTextColorCode)) })
    }
}

