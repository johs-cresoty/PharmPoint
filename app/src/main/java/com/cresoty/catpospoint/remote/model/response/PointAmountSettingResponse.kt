package com.cresoty.catpospoint.remote.model.response

import com.cresoty.catpospoint.data.model.PointAmountSettingEntity
import com.google.gson.annotations.SerializedName

data class PointAmountSettingResponse(
    @SerializedName("MSG")  val message: String?,
    @SerializedName("CODE") val code: String?,
    @SerializedName("DATA") val data: PointAmountSettingData?,
    @SerializedName("DTL")  val detail: String?
)

data class PointAmountSettingData(
    @SerializedName("INFO") val info: List<PointAmountSettingDto>?
)

data class PointAmountSettingDto(
    @SerializedName("BASE_AMT") val baseAmt: String?
)

fun PointAmountSettingResponse.toData(): PointAmountSettingEntity {
    val min = data?.info?.mapNotNull { it.baseAmt?.toIntOrNull() }?.minOrNull() ?: 20000
    return PointAmountSettingEntity(minAmount = min)
}
