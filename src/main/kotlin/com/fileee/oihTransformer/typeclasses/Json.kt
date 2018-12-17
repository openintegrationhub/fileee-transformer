package com.fileee.oihTransformer.typeclasses

import arrow.Kind
import javax.json.JsonObject

interface ToJson<F, A> {
    fun A.toJson(): Kind<F, JsonObject>
}

interface FromJson<F, A> {
    fun JsonObject.fromJson(): Kind<F, A>
}