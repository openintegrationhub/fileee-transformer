package com.fileee.oihTransformer.fileee

import arrow.core.Option
import arrow.data.Nel

class Contact(
        id: Option<UUID>,
        val name: String,
        val logo: Option<String>,
        val addresses: Option<Nel<Address>>,
        val branding: Option<BrandingInfo>,
        val contactData: Option<Nel<ContactData>>
) : Entity(id) {
    companion object {
        // json keys
        const val name = "name"
        const val logo = "logo"
        const val addresses = "addresses"
        const val brandingInfo = "branding"
        const val contactData = "contactData"
    }
}

class Address(
        id: Option<UUID>,
        val street: Option<String>,
        val zipCode: Option<String>,
        val city: Option<String>,
        val countryCode: Option<String>
) : Entity(id) {
    // json keys
    companion object {
        const val street = "street"
        const val zipCode = "zipCode"
        const val city = "city"
        const val countryCode = "countryCode"
    }
}

class BrandingInfo(
        val logoBackgroundColorCode: Option<String>,
        val headerBackgroundColorCode: Option<String>,
        val headerTextColorCode: Option<String>,
        val logoTextColorCode: Option<String>,
        val brandLinkColorCode: Option<String>,
        val brandTextColorCode: Option<String>
) {
    companion object {
        // json keys
        const val logoBackgroundColorCode = "logoBackgroundColorCode"
        const val headerBackgroundColorCode = "headerBackgroundColorCode"
        const val headerTextColorCode = "headerTextColorCode"
        const val logoTextColorCode = "logoTextColorCode"
        const val brandLinkColorCode = "brandLinkColorCode"
        const val brandTextColorCode = "brandTextColorCode"
    }
}

class ContactData(
        val value: String,
        val type: String,
        val description: Option<String>
) {
    companion object {
        // json keys
        const val value = "value"
        const val type = "type"
        const val description = "description"
    }
}