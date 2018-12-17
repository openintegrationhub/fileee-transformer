package com.fileee.oihTransformer.fileee

import arrow.core.Option

typealias UUID = String

abstract class Entity(
        val id: Option<UUID>
) {
    companion object {
        // json keys
        const val id = "id"
    }
}