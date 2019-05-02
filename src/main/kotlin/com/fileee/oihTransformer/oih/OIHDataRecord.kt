package com.fileee.oihTransformer.oih

import arrow.core.Option
import arrow.data.Nel
import com.fileee.oihTransformer.utils.FileeeApplicationId

abstract class OIHDataRecord(
        val oihId: Option<String>,
        val oihCreated: Option<Modification>,
        val oihLastModified: Option<Modification>,
        val oihApplicationRecords: Nel<OIHApplicationRecord>
) {
    companion object {
        const val oihId = "oihId"
        const val oihCreated = "oihCreated"
        const val oihLastModified = "oihLastModified"
        const val oihApplicationRecords = "oihApplicationRecords"
    }
}

// TODO applicationUid. This is currently blocked because there is no final decision on this topic afaik
data class OIHApplicationRecord(
        val applicationUid: String = FileeeApplicationId,
        val recordUid: Option<String>,
        val created: Option<Modification>,
        val lastModified: Option<Modification>,
        val modificationHistory: Option<Nel<Modification>>
) {
    companion object {
        const val applicationUid = "applicationUid"
        const val recordUid = "recordUid"
        const val created = "created"
        const val lastModified = "lastModified"
        const val modificationHistory = "modificationHistory"
    }
}

data class Modification(
        val userId: String,
        val type: String,
        val timestamp: String
) {
    companion object {
        const val userId = "userId"
        const val type = "type"
        const val timestamp = "timestamp"
    }
}