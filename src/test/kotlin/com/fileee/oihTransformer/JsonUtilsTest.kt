package com.fileee.oihTransformer

import com.fileee.oihTransformer.generators.jsonObjectGen
import com.fileee.oihTransformer.utils.*
import io.kotlintest.assertions.arrow.either.beLeft
import io.kotlintest.assertions.arrow.either.beRight
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import javax.json.Json

class TryGetStringSpec : StringSpec({
  "tryGetString should get a string when one is present at the right key" {
    val obj = Json.createObjectBuilder().add("MyKey", "MyString").build()

    val res = obj.tryGetString("MyKey")

    res shouldBe beRight("MyString")
  }
  "tryGetString should fail with the correct error when the variable is of the wrong type" {
    val obj = Json.createObjectBuilder().add("MyKey", 1).build()

    val res = obj.tryGetString("MyKey")

    res shouldBe beLeft(InvalidTypeError("JsonString", "NUMBER"))
  }
  "tryGetString should fail with the correct error for a missing value" {
    val obj = Json.createObjectBuilder().build()

    val res = obj.tryGetString("MyKey")

    res shouldBe beLeft(MissingRequiredValue("MyKey"))
  }
})

class TryGetJsonArraySpec : StringSpec({
  "tryGetJsonArray should get an array when one is present at the right key" {
    val arr = Json.createArrayBuilder().build()
    val obj = Json.createObjectBuilder().add("MyKey", arr).build()

    val res = obj.tryGetJsonArray("MyKey")

    res shouldBe beRight(arr)
  }
  "tryGetJsonArray should fail with an invalid type error if the value at the key is not a jsonarray" {
    val obj = Json.createObjectBuilder().add("MyKey", 1).build()

    val res = obj.tryGetJsonArray("MyKey")

    res shouldBe beLeft(InvalidTypeError("JsonArray", "NUMBER"))
  }
  "tryGetJsonArray should fail with missing error type" {
    val obj = Json.createObjectBuilder().build()

    val res = obj.tryGetJsonArray("MyKey")

    res shouldBe beLeft(MissingRequiredValue("MyKey"))
  }
})

class TryGetJsonObjectSpec : StringSpec({
  "tryGetJsonObject should get an object when one is present at the right key" {
    val obj2 = Json.createObjectBuilder().build()
    val obj = Json.createObjectBuilder().add("MyKey", obj2).build()

    val res = obj.tryGetJsonObject("MyKey")

    res shouldBe beRight(obj2)
  }
  "tryGetJsonObject should fail with the correct error when the value at the key is not a JsonObject" {
    val obj = Json.createObjectBuilder().add("MyKey", 1).build()

    val res = obj.tryGetJsonObject("MyKey")

    res shouldBe beLeft(InvalidTypeError("JsonObject", "NUMBER"))
  }
  "tryGetJsonObject should fail with the correct error when no value is present at the key" {
    val obj = Json.createObjectBuilder().build()

    val res = obj.tryGetJsonObject("MyKey")

    res shouldBe beLeft(MissingRequiredValue("MyKey"))
  }
})

class JsonBuilderSpec : StringSpec({
  "builder should not add null" {
    val obj = JsonBuilder {
      "MyKey" { null }
    }.build()

    obj shouldBe Json.createObjectBuilder().build()
  }

  "builder should correctly add values of supported types" {
    forAll(Gen.string(), Gen.int()) { s, i ->
      val expected = Json.createObjectBuilder()
        .add(s, i)
        .build()

      expected == JsonBuilder {
        s { i }
      }.build()
    }
  }
})