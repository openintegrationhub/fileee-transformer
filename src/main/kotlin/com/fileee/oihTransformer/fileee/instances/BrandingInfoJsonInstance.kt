package com.fileee.oihTransformer.fileee.instances

import arrow.Kind
import arrow.core.*
import com.fileee.oihTransformer.fileee.*
import com.fileee.oihTransformer.fileee.BrandingInfo.Companion.brandLinkColorCode
import com.fileee.oihTransformer.fileee.BrandingInfo.Companion.brandTextColorCode
import com.fileee.oihTransformer.fileee.BrandingInfo.Companion.headerBackgroundColorCode
import com.fileee.oihTransformer.fileee.BrandingInfo.Companion.headerTextColorCode
import com.fileee.oihTransformer.fileee.BrandingInfo.Companion.logoBackgroundColorCode
import com.fileee.oihTransformer.fileee.BrandingInfo.Companion.logoTextColorCode
import com.fileee.oihTransformer.typeclasses.FromJson
import com.fileee.oihTransformer.typeclasses.ToJson
import com.fileee.oihTransformer.utils.JsonBuilder
import com.fileee.oihTransformer.utils.ParseError
import com.fileee.oihTransformer.utils.optional
import com.fileee.oihTransformer.utils.tryGetString
import javax.json.JsonObject

object BrandingInfoToJsonInstance : ToJson<ForId, BrandingInfo> {
    override fun BrandingInfo.toJson(): Kind<ForId, JsonObject> = Id.just(
            convertJson(this)
    )

    fun convertJson(branding: BrandingInfo): JsonObject =
            JsonBuilder {
                logoBackgroundColorCode { branding.logoBackgroundColorCode.orNull() }
                logoTextColorCode { branding.logoTextColorCode.orNull() }
                headerBackgroundColorCode { branding.headerBackgroundColorCode.orNull() }
                headerTextColorCode { branding.headerTextColorCode.orNull() }
                brandTextColorCode { branding.brandTextColorCode.orNull() }
                brandLinkColorCode { branding.brandLinkColorCode.orNull() }
            }.build()
}

fun BrandingInfo.Companion.toJson() = BrandingInfoToJsonInstance

object BrandingInfoFromJsonInstance : FromJson<EitherPartialOf<ParseError>, BrandingInfo> {
    override fun JsonObject.fromJson(): Kind<EitherPartialOf<ParseError>, BrandingInfo> = Either.applicative<ParseError>().map(
            tryGetString(logoBackgroundColorCode).optional(),
            tryGetString(headerBackgroundColorCode).optional(),
            tryGetString(brandTextColorCode).optional(),
            tryGetString(brandLinkColorCode).optional(),
            tryGetString(logoTextColorCode).optional(),
            tryGetString(headerTextColorCode).optional()
    ) { (logoBackgroundColorCode, headerBackgroundColorCode, brandTextColorCode, brandLinkColorCode, logoTextColorCode, headerTextColorCode) ->
        BrandingInfo(
                logoBackgroundColorCode = logoBackgroundColorCode,
                headerBackgroundColorCode = headerBackgroundColorCode,
                brandTextColorCode = brandTextColorCode,
                brandLinkColorCode = brandLinkColorCode,
                logoTextColorCode = logoTextColorCode,
                headerTextColorCode = headerTextColorCode
        )
    }.fix()
}

fun BrandingInfo.Companion.fromJson() = BrandingInfoFromJsonInstance