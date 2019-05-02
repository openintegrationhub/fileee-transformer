package com.fileee.oihTransformer.fileee

import arrow.core.Option
import arrow.data.Nel
import com.fileee.oihTransformer.fileee.instances.toJson

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

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Contact

    if (name != other.name) return false
    if (logo != other.logo) return false
    if (addresses != other.addresses) return false
    if (branding != other.branding) return false
    if (contactData != other.contactData) return false

    return true
  }

  override fun hashCode(): Int {
    var result = name.hashCode()
    result = 31 * result + logo.hashCode()
    result = 31 * result + addresses.hashCode()
    result = 31 * result + branding.hashCode()
    result = 31 * result + contactData.hashCode()
    return result
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

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Address

    if (street != other.street) return false
    if (zipCode != other.zipCode) return false
    if (city != other.city) return false
    if (countryCode != other.countryCode) return false

    return true
  }

  override fun hashCode(): Int {
    var result = street.hashCode()
    result = 31 * result + zipCode.hashCode()
    result = 31 * result + city.hashCode()
    result = 31 * result + countryCode.hashCode()
    return result
  }


}

data class BrandingInfo(
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

data class ContactData(
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