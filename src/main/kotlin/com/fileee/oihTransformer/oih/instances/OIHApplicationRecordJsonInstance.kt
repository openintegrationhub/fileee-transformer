package com.fileee.oihTransformer.oih.instances

import arrow.Kind
import arrow.core.*
import com.fileee.oihTransformer.oih.Modification
import com.fileee.oihTransformer.oih.OIHApplicationRecord
import com.fileee.oihTransformer.oih.OIHApplicationRecord.Companion.applicationUid
import com.fileee.oihTransformer.oih.OIHApplicationRecord.Companion.created
import com.fileee.oihTransformer.oih.OIHApplicationRecord.Companion.lastModified
import com.fileee.oihTransformer.oih.OIHApplicationRecord.Companion.modificationHistory
import com.fileee.oihTransformer.oih.OIHApplicationRecord.Companion.recordUid
import com.fileee.oihTransformer.oih.instances.ModificationToJsonInstance.toJson
import com.fileee.oihTransformer.typeclasses.FromJson
import com.fileee.oihTransformer.typeclasses.ToJson
import com.fileee.oihTransformer.utils.*
import javax.json.Json
import javax.json.JsonObject

object OIHApplicationRecordToJsonInstance : ToJson<ForId, OIHApplicationRecord> {
    override fun OIHApplicationRecord.toJson(): Kind<ForId, JsonObject> = Id.just(
            convertJson(this)
    )

    fun convertJson(appRecord: OIHApplicationRecord): JsonObject = JsonBuilder {
        applicationUid { appRecord.applicationUid }
        recordUid { appRecord.recordUid.orNull() }
        lastModified { Modification.toJson().run { appRecord.lastModified.map { it.toJson().value() }.orNull() } }
        created { Modification.toJson().run { appRecord.created.map { it.toJson().value() }.orNull() } }
        modificationHistory {
            appRecord.modificationHistory.map {
                it.foldLeft(Json.createArrayBuilder()) { acc, mod ->
                    acc.add(Modification.toJson().run { mod.toJson().value() })
                }.build()
            }.orNull()
        }
    }.build()
}

fun OIHApplicationRecord.Companion.toJson() = OIHApplicationRecordToJsonInstance

object OIHApplicationRecordFromJsonInstance : FromJson<EitherPartialOf<ParseError>, OIHApplicationRecord> {
    override fun JsonObject.fromJson(): Kind<EitherPartialOf<ParseError>, OIHApplicationRecord> = Either.applicative<ParseError>().map(
            tryGetString(applicationUid),
            tryGetString(recordUid).optional(),
            tryGetModification(lastModified).optional(),
            tryGetModification(created).optional(),
            tryGetModifications(modificationHistory).optional()
    ) { (appUid, recordUid, lastMod, created, modHist) ->
        OIHApplicationRecord(
                applicationUid = appUid,
                recordUid = recordUid,
                lastModified = lastMod,
                created = created,
                modificationHistory = modHist.flatMap { it }
        )
    }

    private fun JsonObject.tryGetModifications(name: String) = tryGetJsonArray(name).flatMap {
        it.map { it.asObject().flatMap { Modification.fromJson().run { it.fromJson().fix() } } }.foldErrors()
    }
}

fun OIHApplicationRecord.Companion.fromJson() = OIHApplicationRecordFromJsonInstance