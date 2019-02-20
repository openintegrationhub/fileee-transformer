package com.fileee.oihTransformer.generators

import arrow.core.Option
import arrow.core.toOption
import arrow.data.Nel
import com.fileee.oihTransformer.fileee.Address
import com.fileee.oihTransformer.fileee.BrandingInfo
import com.fileee.oihTransformer.fileee.Contact
import com.fileee.oihTransformer.fileee.ContactData
import com.fileee.oihTransformer.instances.contactDataFromBrandingInfo
import com.fileee.oihTransformer.oih.*
import io.kotlintest.properties.Gen
import io.kotlintest.properties.PropertyContext
import io.kotlintest.properties.forAll
import javax.json.*

fun <A>forFew(genA: Gen<A>, f: PropertyContext.(A) -> Boolean): Unit = forAll(100, genA, f)

fun <T> Gen<T>.rand() = random().first()

fun <T> Gen<T>.option(): Gen<Option<T>> =
  orNull().map { it.toOption() }

fun <T> Gen<T>.nel(): Gen<Nel<T>> =
  Gen.create {
    Nel.fromListUnsafe(
      (0..Gen.choose(1, 100).rand())
        .map { rand() }
    )
  }

// Generate random valid json
val jsonBoolGen: Gen<JsonValue> = Gen.bool().map { b ->
  if (b) JsonValue.TRUE
  else JsonValue.FALSE
}

val jsonStringGen: Gen<JsonValue> = Gen.string().map { s ->
  Json.createObjectBuilder().add("1", s).build()["1"]!!
}

val jsonNumberGen: Gen<JsonValue> = Gen.int().map { i ->
  Json.createObjectBuilder().add("1", i).build()["1"]!!
}

val jsonArrayGen: Gen<JsonArray> = object : Gen<JsonArray> {
  override fun constants(): Iterable<JsonArray> =
    listOf(Json.createArrayBuilder().build())

  override fun random(): Sequence<JsonArray> =
    generateSequence {
      val build = Json.createArrayBuilder()
      for (i in 1..Gen.choose(0, 10).random().first()) {
        build.add(jsonValueGen.random().first())
      }
      build.build()
    }
}

val jsonSimpleArrayGen: Gen<JsonArray> = object : Gen<JsonArray> {
  override fun constants(): Iterable<JsonArray> =
    listOf(Json.createArrayBuilder().build())

  override fun random(): Sequence<JsonArray> =
    generateSequence {
      val build = Json.createArrayBuilder()
      for (i in 1..Gen.choose(0, 10).random().first()) {
        build.add(jsonSimpleValueGen.random().first())
      }
      build.build()
    }

}

val jsonObjectGen: Gen<JsonObject> = object : Gen<JsonObject> {
  override fun random(): Sequence<JsonObject> =
    generateSequence {
      val build = Json.createObjectBuilder()
      for (i in 1..Gen.choose(0, 10).random().first()) {
        build.add(Gen.string().random().first(), jsonValueGen.random().first())
      }
      build.build()
    }

  override fun constants(): Iterable<JsonObject> = listOf(
    Json.createObjectBuilder().build()
  )
}

val jsonSimpleObjGen: Gen<JsonObject> = object : Gen<JsonObject> {
  override fun constants(): Iterable<JsonObject> =
    listOf(Json.createObjectBuilder().build())

  override fun random(): Sequence<JsonObject> =
    generateSequence {
      val build = Json.createObjectBuilder()
      for (i in 1..Gen.choose(0, 10).random().first()) {
        build.add(Gen.string().random().first(), jsonSimpleValueGen.random().first())
      }
      build.build()
    }

}

val jsonValueGen: Gen<JsonValue> = Gen.oneOf(
  jsonBoolGen,
  jsonStringGen,
  jsonNumberGen,
  Gen.create { JsonValue.NULL },
  jsonSimpleObjGen.map { it as JsonValue },
  jsonSimpleArrayGen.map { it as JsonValue }
)

val jsonSimpleValueGen: Gen<JsonValue> = Gen.oneOf(
  jsonBoolGen,
  jsonStringGen,
  jsonNumberGen,
  Gen.create { JsonValue.NULL }
)

fun JsonObject.toBuilder(): JsonObjectBuilder {
  val build = Json.createObjectBuilder()
  mapValues { (k, v) ->
    build.add(k, v)
  }
  return build
}

// fileee contact generator
val brandingGen: Gen<BrandingInfo> = object : Gen<BrandingInfo> {
  override fun constants(): Iterable<BrandingInfo> = emptyList()

  override fun random(): Sequence<BrandingInfo> =
    generateSequence {
      BrandingInfo(
        logoTextColorCode = Gen.string().option().rand(),
        logoBackgroundColorCode = Gen.string().option().rand(),
        headerTextColorCode = Gen.string().option().rand(),
        headerBackgroundColorCode = Gen.string().option().rand(),
        brandTextColorCode = Gen.string().option().rand(),
        brandLinkColorCode = Gen.string().option().rand()
      )
    }
}

val addressGen: Gen<Address> = object : Gen<Address> {
  override fun constants(): Iterable<Address> = emptyList()

  override fun random(): Sequence<Address> =
    generateSequence {
      Address(
        id = Gen.string().option().rand(),
        zipCode = Gen.string().option().rand(),
        countryCode = Gen.string().option().rand(),
        city = Gen.string().option().rand(),
        street = Gen.string().option().rand()
      )
    }
}

val contactDataGen : Gen<ContactData> = object : Gen<ContactData> {
  override fun constants(): Iterable<ContactData> = emptyList()

  override fun random(): Sequence<ContactData> =
    generateSequence {
      ContactData(
        value = Gen.string().rand(),
        type = Gen.string().rand(),
        description = Gen.string().option().rand()
      )
    }

}

val contactGen: Gen<Contact> = object : Gen<Contact> {
  override fun constants(): Iterable<Contact> = emptyList()

  override fun random(): Sequence<Contact> =
    generateSequence {
      Contact(
        id = Gen.string().option().rand(),
        logo = Gen.string().option().rand(),
        name = Gen.string().rand(),
        branding = brandingGen.option().rand(),
        addresses = addressGen.nel().option().rand(),
        contactData = contactDataGen.nel().option().rand()
      )
    }
}

// oih contact TODO constrain some generators like timestamp
val modificationGen : Gen<Modification> = object : Gen<Modification> {
  override fun constants(): Iterable<Modification> = emptyList()

  override fun random(): Sequence<Modification> =
    generateSequence {
      Modification(
        userId = Gen.string().rand(),
        type = Gen.string().rand(),
        timestamp = Gen.string().rand()
      )
    }
}

val oihContactDataGen : Gen<OIHContactData> = object : Gen<OIHContactData> {
  override fun constants(): Iterable<OIHContactData> = emptyList()

  override fun random(): Sequence<OIHContactData> =
    generateSequence {
      OIHContactData(
        value = Gen.string().rand(),
        type = Gen.string().rand(),
        description = Gen.string().option().rand()
      )
    }
}

val oihAddressGen : Gen<OIHAddress> = object : Gen<OIHAddress> {
  override fun constants(): Iterable<OIHAddress> = emptyList()

  override fun random(): Sequence<OIHAddress> =
    generateSequence {
      OIHAddress(
        description = Gen.string().option().rand(),
        street = Gen.string().option().rand(),
        city = Gen.string().option().rand(),
        countryCode = Gen.string().option().rand(),
        zipCode = Gen.string().option().rand(),
        country = Gen.string().option().rand(),
        district = Gen.string().option().rand(),
        unit = Gen.string().option().rand(),
        region = Gen.string().option().rand(),
        primaryContact = Gen.string().option().rand(),
        streetNumber = Gen.string().option().rand()
      )
    }
}

val oihApplicationRecordGen : Gen<OIHApplicationRecord> = object : Gen<OIHApplicationRecord> {
  override fun constants(): Iterable<OIHApplicationRecord> = emptyList()

  override fun random(): Sequence<OIHApplicationRecord> =
    generateSequence {
      OIHApplicationRecord(
        applicationUid = Gen.string().rand(),
        lastModified = modificationGen.option().rand(),
        created = modificationGen.option().rand(),
        recordUid = Gen.string().option().rand(),
        modificationHistory = modificationGen.nel().option().rand()
      )
    }
}

val oihPersonGen : Gen<OIHContact.OIHPerson> = object : Gen<OIHContact.OIHPerson> {
  override fun constants(): Iterable<OIHContact.OIHPerson> = emptyList()

  override fun random(): Sequence<OIHContact.OIHPerson> =
    generateSequence {
      OIHContact.OIHPerson(
        oihId = Gen.string().option().rand(),
        firstName = Gen.string().option().rand(),
        lastName = Gen.string().option().rand(),
        oihCreated = modificationGen.option().rand(),
        oihLastModified = modificationGen.option().rand(),
        contactData = oihContactDataGen.nel().option().rand(),
        addresses = oihAddressGen.nel().option().rand(),
        oihApplicationRecords = oihApplicationRecordGen.nel().rand()
      )
    }
}

val oihOrganizationGen : Gen<OIHContact.OIHOrganization> = object : Gen<OIHContact.OIHOrganization> {
  override fun constants(): Iterable<OIHContact.OIHOrganization> = emptyList()

  override fun random(): Sequence<OIHContact.OIHOrganization> =
    generateSequence {
      OIHContact.OIHOrganization(
        name = Gen.string().option().rand(),
        logo = Gen.string().option().rand(),
        oihId = Gen.string().option().rand(),
        oihCreated = modificationGen.option().rand(),
        oihLastModified = modificationGen.option().rand(),
        contactData = oihContactDataGen.nel().option().rand(),
        addresses = oihAddressGen.nel().option().rand(),
        oihApplicationRecords = oihApplicationRecordGen.nel().rand()
      )
    }
}

val oihContactGen : Gen<OIHContact> = Gen.oneOf(oihPersonGen.map { it as OIHContact }, oihOrganizationGen.map { it as OIHContact })

val oihContactDataBrandingInfoGen : Gen<OIHContactData> = Gen.oneOf(
  oihContactDataGen,
  Gen.from(brandingGen.map { contactDataFromBrandingInfo(it) }.rand())
)