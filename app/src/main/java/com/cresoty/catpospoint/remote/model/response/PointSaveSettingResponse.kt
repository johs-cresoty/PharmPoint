package com.cresoty.catpospoint.remote.model.response

import com.cresoty.catpospoint.data.model.PointSaveSettingEntity
import com.google.gson.annotations.SerializedName

data class PointSaveSettingResponse(
    @SerializedName("MSG")  val message: String?,
    @SerializedName("CODE") val code: String?,
    @SerializedName("DATA") val data: PointSaveSettingData?,
    @SerializedName("DTL")  val detail: String?
)

data class PointSaveSettingData(
    @SerializedName("INFO") val info: List<PointSaveSettingDto>?
)

data class PointSaveSettingDto(
    @SerializedName("PNT_GUBN") val pointGubn: String?
)

fun PointSaveSettingResponse.toData(): PointSaveSettingEntity =
    PointSaveSettingEntity(
        isSave = (data?.info?.firstOrNull()?.pointGubn ?: "") != "NON"
    )
