# Fileee Openintegrationhub transformer

> Fileee data transformer for the OIH data model

With fileee one can import documents using either the app or specific import functions from within fileee.
Importing documents with services such as Dropbox, GoogleDrive and WebDav is also supported.

Important information like document-type, sender or invoice amount is then automatically extracted by fileee.
Using tags, an intelligent sorting and full text search documents remain easily accessible. Exporting via pdf is also possible.
Using extracted information fileee also provides intelligent reminders for appointments and invoices.
Documents can also be shared easily to one or more users with fileeeSpaces.
Every document is separately encrypted, is only accessible to the user and stored in a german data center.

## Adapter

This component is made to be used together with the **fileee-openintegrationhub-adapter**. (`1.0.0`)

## OIH data model

This transformer uses the contact data model, specifically the **organization** model from the address model. (`V2`)

## Domain objects

Currently the transformer operates solely on the address-model, more specifically it's output (transforming to oih) will always use the **organization** model.

## Available actions

- transformPersonToOIH Transform a fileee person/contact to an oih contact
- transformPersonFromOIH Transform a oih contact to a fileee person/contact

## How it works

The transformer is written in kotlin and uses a concept from functional programming called typeclasses to model capabilities of data.
In this case the data classes for `FileeeContact` and all it's non-primitve properties have instances of the `ToJson`, `FromJson`, `TransformOIH` typeclass which models its transformation.
This approach leads to a type safe conversion that can easily catch and handle errors and can easily be combined with other instances to form larger more complex serializers.

An instance of `ToJson` for a oih modification object looks like this: (using a simply json builder)
```kotlin
object ModificationToJsonInstance: ToJson<ForId, Modification> {
    override fun Modification.toJson(): Kind<ForId, JsonObject> = Id.just(JsonBuilder {
        "userId" { userId }
        "timestamp" { timestamp }
        "type" { type }
    }.build())
}
```
> The `ForId` states that the modification is returned in an Id datatype and no effects may occur when serializing. (Effects may include error handling, side-effects, io, ...)

An instance of `FromJson` for the same object:
```kotlin
object ModificationFromJsonInstance: FromJson<EitherPartialOf<ParseError>, Modification> {
    override fun JsonObject.fromJson(): Kind<EitherPartialOf<ParseError>, Modification> = Either.applicative<ParseError>().map(
            tryGetString("userId"),
            tryGetString("timestamp"),
            tryGetString("type")
    ) { (userId, timestamp, type) -> Modification(
            userId = userId,
            timestamp = timestamp,
            type = type
    ) }
}
```
> Note: the `EitherPartialOf<ParseError>` instead of id. This describes our return type as either an error or a value.
The value in this case being an instance of Modification. This is very useful because obviously parsing json might fail.

> Note: The `Èither.applicative<ParseError>` might also look rather confusing. It is a reference to the instance of `Applicative` for `Either`.
The function `map` that this typeclass defines let's us accumulate Either values, and if we only get right values perform an operation on all of them.
If at any point one of these Either's is a left either containing an error the resulting either will also be a left either and the operation below is not run.

Now an instance of `TransformOIH` for contacts: (Shortened version)
```kotlin
object ContactTransformOIHInstance : TransformOIH<EitherPartialOf<ParseError>, Contact, OIHContact> {
    override fun Contact.transformToOIH(): Kind<EitherPartialOf<ParseError>, OIHContact> =
            OIHContact(
                    name = name,
                    logo = logo,
                    addresses = addresses.map { it.map { transformAddressToOIH(it) } },
                    contactData = contactData.map {
                        it.map { transformContactDataToOIH(it) } + branding.fold({ emptyList<OIHContactData>() }, { contactDataFromBrandingInfo(it) })
                    },
                    oihApplicationRecords = OIHApplicationRecord(
                            recordUid = id,
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
    
    // ...
}
```
> The transform instance operates on the Either effect because when converting to fileee a name is required. Hence conversion may fail which is modeled by Either.