package com.fileee.oihTransformer.oih

import arrow.core.Option
import arrow.core.none
import arrow.data.Nel

sealed class OIHContact(
        val addresses: Option<Nel<OIHAddress>>,
        val contactData: Option<Nel<OIHContactData>>,
        oihId: Option<String> = none(),
        oihCreated: Option<Modification> = none(),
        oihLastModified: Option<Modification> = none(),
        oihApplicationRecords: Nel<OIHApplicationRecord>
) : OIHDataRecord(
        oihId = oihId,
        oihApplicationRecords = oihApplicationRecords,
        oihCreated = oihCreated,
        oihLastModified = oihLastModified
) {
    class OIHOrganization(
            val name: Option<String>,
            val logo: Option<String>,
            addresses: Option<Nel<OIHAddress>>,
            contactData: Option<Nel<OIHContactData>>,
            oihId: Option<String> = none(),
            oihCreated: Option<Modification> = none(),
            oihLastModified: Option<Modification> = none(),
            oihApplicationRecords: Nel<OIHApplicationRecord>
    ) : OIHContact(
            addresses = addresses,
            contactData = contactData,
            oihId = oihId,
            oihApplicationRecords = oihApplicationRecords,
            oihCreated = oihCreated,
            oihLastModified = oihLastModified
    ) {
        companion object {
            const val name = "name"
            const val logo = "logo"
        }
    }

    class OIHPerson(
            val firstName: Option<String>,
            val lastName: Option<String>,
            addresses: Option<Nel<OIHAddress>>,
            contactData: Option<Nel<OIHContactData>>,
            oihId: Option<String> = none(),
            oihCreated: Option<Modification> = none(),
            oihLastModified: Option<Modification> = none(),
            oihApplicationRecords: Nel<OIHApplicationRecord>
    ) : OIHContact(
            addresses = addresses,
            contactData = contactData,
            oihId = oihId,
            oihApplicationRecords = oihApplicationRecords,
            oihCreated = oihCreated,
            oihLastModified = oihLastModified
    ) {
        companion object {
            const val firstName = "firstName"
            const val lastName = "lastName"
        }
    }

    companion object {
        const val addresses = "addresses"
        const val contactData = "contactData"
    }
}

class OIHAddress(
        val street: Option<String>,
        val streetNumber: Option<String>,
        val unit: Option<String>,
        val zipCode: Option<String>,
        val city: Option<String>,
        val district: Option<String>,
        val region: Option<String>,
        val country: Option<String>,
        val countryCode: Option<String>,
        val primaryContact: Option<String>,
        val description: Option<String>
) {
    companion object {
        const val street = "street"
        const val streetNumber = "streetNumber"
        const val unit = "unit"
        const val zipCode = "zipCode"
        const val city = "city"
        const val district = "district"
        const val region = "region"
        const val country = "country"
        const val countryCode = "countryCode"
        const val primaryContact = "primaryContact"
        const val description = "description"
    }
}

class OIHContactData(
        val value: String,
        val type: String,
        val description: Option<String> = none()
) {
    companion object {
        const val value = "value"
        const val type = "type"
        const val description = "description"
    }
}