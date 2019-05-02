package com.fileee.oihTransformer.instances

import arrow.typeclasses.Eq
import com.fileee.oihTransformer.fromJsonToJson
import com.fileee.oihTransformer.generators.oihAddressGen
import com.fileee.oihTransformer.generators.oihApplicationRecordGen
import com.fileee.oihTransformer.generators.oihContactDataGen
import com.fileee.oihTransformer.generators.oihContactGen
import com.fileee.oihTransformer.oih.OIHAddress
import com.fileee.oihTransformer.oih.OIHApplicationRecord
import com.fileee.oihTransformer.oih.OIHContact
import com.fileee.oihTransformer.oih.OIHContactData
import com.fileee.oihTransformer.oih.instances.fromJson
import com.fileee.oihTransformer.oih.instances.toJson
import com.fileee.oihTransformer.toJsonFromJson
import io.kotlintest.specs.StringSpec

class OIHContactInstancesSpec : StringSpec({
  "toJsonFromJson relation" {
    toJsonFromJson(oihContactGen, Eq.any(), OIHContact.fromJson(), OIHContact.toJson())
  }
  "fromJsonToJson relation" {
    fromJsonToJson(oihContactGen, OIHContact.fromJson(), OIHContact.toJson())
  }
})

class OIHApplicationRecordInstancesSpec : StringSpec({
  "toJsonFromJson relation" {
    toJsonFromJson(oihApplicationRecordGen, Eq.any(), OIHApplicationRecord.fromJson(), OIHApplicationRecord.toJson())
  }
  "fromJsonToJson relation" {
    fromJsonToJson(oihApplicationRecordGen, OIHApplicationRecord.fromJson(), OIHApplicationRecord.toJson())
  }
})

class OIHAddressInstancesSpec : StringSpec({
  "toJsonFromJson relation" {
    toJsonFromJson(oihAddressGen, Eq.any(), OIHAddress.fromJson(), OIHAddress.toJson())
  }
  "fromJsonToJson relation" {
    fromJsonToJson(oihAddressGen, OIHAddress.fromJson(), OIHAddress.toJson())
  }
})

class OIHContactDataInstancesSpec : StringSpec({
  "toJsonFromJson relation" {
    toJsonFromJson(oihContactDataGen, Eq.any(), OIHContactData.fromJson(), OIHContactData.toJson())
  }
  "fromJsonToJson relation" {
    fromJsonToJson(oihContactDataGen, OIHContactData.fromJson(), OIHContactData.toJson())
  }
})