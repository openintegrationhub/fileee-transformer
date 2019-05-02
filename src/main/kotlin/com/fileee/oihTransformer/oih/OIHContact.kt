package com.fileee.oihTransformer.oih

import arrow.core.Option
import arrow.core.none
import arrow.core.or
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

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      // special case where both are empty and thus logically equal
      if (other is OIHPerson && name.or(logo).or(other.firstName).or(other.lastName).isEmpty()) return true
      if (javaClass != other?.javaClass) return false

      other as OIHOrganization

      if (name != other.name) return false
      if (logo != other.logo) return false

      return true
    }

    override fun hashCode(): Int {
      var result = name.hashCode()
      result = 31 * result + logo.hashCode()
      return result
    }

    override fun toString(): String {
      return "OIHOrganization(name=$name, logo=$logo)"
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

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      // special case where both are empty and thus logically equal
      if (other is OIHOrganization && other.name.or(other.logo).or(firstName).or(lastName).isEmpty()) return true
      if (javaClass != other?.javaClass) return false

      other as OIHPerson

      if (firstName != other.firstName) return false
      if (lastName != other.lastName) return false

      return true
    }

    override fun hashCode(): Int {
      var result = firstName.hashCode()
      result = 31 * result + lastName.hashCode()
      return result
    }

    override fun toString(): String {
      return "OIHPerson(firstName=$firstName, lastName=$lastName)"
    }

  }

  companion object {
    const val addresses = "addresses"
    const val contactData = "contactData"
  }
}

data class OIHAddress(
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

data class OIHContactData(
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