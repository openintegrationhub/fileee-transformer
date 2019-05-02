package com.fileee.oihTransformer.instances

import arrow.typeclasses.Eq
import com.fileee.oihTransformer.fileee.Address
import com.fileee.oihTransformer.fileee.BrandingInfo
import com.fileee.oihTransformer.fileee.Contact
import com.fileee.oihTransformer.fileee.ContactData
import com.fileee.oihTransformer.fileee.instances.fromJson
import com.fileee.oihTransformer.fileee.instances.toJson
import com.fileee.oihTransformer.fromJsonToJson
import com.fileee.oihTransformer.generators.addressGen
import com.fileee.oihTransformer.generators.brandingGen
import com.fileee.oihTransformer.generators.contactDataGen
import com.fileee.oihTransformer.generators.contactGen
import com.fileee.oihTransformer.toJsonFromJson
import io.kotlintest.specs.StringSpec

class ContactInstancesSpec : StringSpec({
  "toJsonFromJson relation" {
    toJsonFromJson(contactGen, Eq.any(), Contact.fromJson(), Contact.toJson())
  }
  "fromJsonToJson relation" {
    fromJsonToJson(contactGen, Contact.fromJson(), Contact.toJson())
  }
})


class AddressInstancesSpec : StringSpec({
  "toJsonFromJson relation" {
    toJsonFromJson(addressGen, Eq.any(), Address.fromJson(), Address.toJson())
  }
  "fromJsonToJson relation" {
    fromJsonToJson(addressGen, Address.fromJson(), Address.toJson())
  }
})

class BrandingInfoInstancesSpec : StringSpec({
  "toJsonFromJson relation" {
    toJsonFromJson(brandingGen, Eq.any(), BrandingInfo.fromJson(), BrandingInfo.toJson())
  }
  "fromJsonToJson relation" {
    fromJsonToJson(brandingGen, BrandingInfo.fromJson(), BrandingInfo.toJson())
  }
})

class ContactDataInstancesSpec : StringSpec({
  "toJsonFromJson relation" {
    toJsonFromJson(contactDataGen, Eq.any(), ContactData.fromJson(), ContactData.toJson())
  }
  "fromJsonToJson relation" {
    fromJsonToJson(contactDataGen, ContactData.fromJson(), ContactData.toJson())
  }
})