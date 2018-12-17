package com.fileee.oihTransformer.typeclasses

import arrow.Kind
import com.fileee.oihTransformer.fileee.Entity
import com.fileee.oihTransformer.oih.OIHDataRecord

interface TransformOIH<F, A: Entity, B: OIHDataRecord> {
    fun A.transformToOIH(): Kind<F, B>
    fun B.transformToFileee(): Kind<F, A>
}