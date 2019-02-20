package com.fileee.oihTransformer.instances

import arrow.core.None
import arrow.core.fix
import arrow.core.or
import arrow.core.toOption
import arrow.data.Nel
import com.fileee.oihTransformer.fileee.BrandingInfo
import com.fileee.oihTransformer.fileee.BrandingInfo.Companion.brandLinkColorCode
import com.fileee.oihTransformer.fileee.BrandingInfo.Companion.brandTextColorCode
import com.fileee.oihTransformer.fileee.BrandingInfo.Companion.headerBackgroundColorCode
import com.fileee.oihTransformer.fileee.BrandingInfo.Companion.headerTextColorCode
import com.fileee.oihTransformer.fileee.BrandingInfo.Companion.logoBackgroundColorCode
import com.fileee.oihTransformer.fileee.BrandingInfo.Companion.logoTextColorCode
import com.fileee.oihTransformer.generators.*
import com.fileee.oihTransformer.oih.OIHApplicationRecord
import com.fileee.oihTransformer.oih.OIHContact
import com.fileee.oihTransformer.oih.OIHContactData
import com.fileee.oihTransformer.utils.FileeeApplicationId
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.specs.StringSpec

class ContactTransformSpec : StringSpec({

  // every fileee contact is an oih contact (however there is no isomorphism)
  "transformFileeeContactToOIH" {
    forFew(contactGen) { contact ->
      ContactTransformOIHInstance.run {
        contact.transformToOIH().fix().fold({ false }, { true })
      }
    }
  }

  // every oih contact with at least a name is a fileee contact
  "transformOIHContactToFileee" {
    forFew(oihContactGen.filter { when (it) {
      is OIHContact.OIHOrganization -> it.name.isDefined()
      is OIHContact.OIHPerson -> it.firstName.or(it.lastName).isDefined()
    } }) { oihContact ->
      ContactTransformOIHInstance.run {
        oihContact.transformToFileee().fix().fold({ false }, { true })
      }
    }
  }

  "findFileeeRecordId" {
    forFew(oihApplicationRecordGen.nel()) { rec: Nel<OIHApplicationRecord> ->
      rec.all.find { it.applicationUid == FileeeApplicationId }.toOption().fold({
        findFileeeRecordId(rec).isEmpty()
      }, { record ->
        record.recordUid.fold({
          findFileeeRecordId(rec).isEmpty()
        }, { uuid ->
          findFileeeRecordId(rec).fold({ false }, { it == uuid })
        })
      })
    }
  }

  "transformAddressToFileee" {
    forFew(oihAddressGen) { oihAddr ->
      val res = transformAddressToFileee(oihAddr)
      res.id.isEmpty() &&
        res.city == oihAddr.city &&
        res.countryCode == oihAddr.countryCode &&
        res.zipCode == oihAddr.zipCode &&
        res.street == oihAddr.street.flatMap { str -> oihAddr.streetNumber.map { "$str $it" } }
    }
  }

  "isBrandingInfo" {
    forFew(oihContactDataBrandingInfoGen) { contactData ->
      isBrandingInfo(contactData) == (
        setOf(BrandingInfo.brandLinkColorCode, BrandingInfo.brandTextColorCode, BrandingInfo.headerBackgroundColorCode, BrandingInfo.headerTextColorCode, BrandingInfo.logoBackgroundColorCode, BrandingInfo.logoTextColorCode)
          .contains(contactData.type)
        )
    }
  }

  "transformContactDataToFileee" {
    forAll(oihContactDataGen) { contactData ->
      val result = transformContactDataToFileee(contactData)
      result.type == contactData.type.toUpperCase() &&
        result.value == contactData.value &&
        result.description == contactData.description
    }

    "brandingInfoFromContactData" {
      forFew(Gen.oneOf(
        oihContactDataBrandingInfoGen.nel().map { it.all },
        Gen.create { emptyList<OIHContactData>() }
      )) { contactData ->
        val res = brandingInfoFromContactData(contactData)
        res.fold({
          contactData.filter { isBrandingInfo(it) }.isEmpty()
        }, { branding ->
          branding.logoTextColorCode == contactData.find { it.type == logoTextColorCode }.toOption() &&
            branding.logoBackgroundColorCode == contactData.find { it.type == logoBackgroundColorCode }.toOption() &&
            branding.brandTextColorCode == contactData.find { it.type == brandTextColorCode }.toOption() &&
            branding.brandLinkColorCode == contactData.find { it.type == brandLinkColorCode }.toOption() &&
            branding.headerTextColorCode == contactData.find { it.type == headerTextColorCode }.toOption() &&
            branding.headerBackgroundColorCode == contactData.find { it.type == headerTextColorCode }.toOption()
        })
      }
    }

    "transformAddressToOIH" {
      forFew(addressGen) { addr ->
        val res = transformAddressToOIH(addr)
        res.city == addr.city &&
          res.country == None &&
          res.countryCode == addr.countryCode &&
          res.description == None &&
          res.district == None &&
          res.primaryContact == None &&
          res.region == None &&
          res.street == addr.street &&
          res.streetNumber == None &&
          res.unit == None &&
          res.zipCode == addr.zipCode
      }
    }

    "transformContactDataToOIH" {
      forAll(contactDataGen) { contactData ->
        val res = transformContactDataToOIH(contactData)
        res.type == contactData.type.toLowerCase() &&
          res.description == contactData.description &&
          res.value == contactData.value
      }
    }

    "contactDataFromBrandingInfo" {
      forFew(brandingGen) { brand ->
        val res = contactDataFromBrandingInfo(brand)
        res.fold(true) { acc, v ->
          acc && isBrandingInfo(v) && when (v.type) {
            logoTextColorCode -> brand.logoTextColorCode.fold({ false }, { it == v.value })
            logoBackgroundColorCode -> brand.logoBackgroundColorCode.fold({ false }, { it == v.value })
            headerTextColorCode -> brand.headerTextColorCode.fold({ false }, { it == v.value })
            headerBackgroundColorCode -> brand.headerBackgroundColorCode.fold({ false }, { it == v.value })
            brandTextColorCode -> brand.brandTextColorCode.fold({ false }, { it == v.value })
            brandLinkColorCode -> brand.brandLinkColorCode.fold({ false }, { it == v.value })
            else -> false
          }
        }
      }
    }
  }
})