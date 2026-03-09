package com.cresoty.catpospoint.remote.model.response

import com.cresoty.catpospoint.data.model.PointBalanceEntity
import com.google.gson.annotations.SerializedName

data class PointBalanceResponse(
    @SerializedName("MSG") val message: String?,
    @SerializedName("CODE") val code: String?,
    @SerializedName("DATA") val data: PointBalanceData?,
    @SerializedName("DTL") val detail: String?
)

data class PointBalanceData(
    @SerializedName("INFO") val info: List<PointBalanceDto>?
)

data class PointBalanceDto(
    @SerializedName("CST_GNDR") val customerGender: String?,
    @SerializedName("PNT_BLC") val pointBalance: String?,
    @SerializedName("CST_BRTH") val customerBirth: String?,
    @SerializedName("CST_NAME") val customerName: String?,
    @SerializedName("CST_CODE") val customerCode: String?,
    @SerializedName("CST_HP") val customerPhone: String?
)

fun PointBalanceResponse.toData(): PointBalanceEntity {
    val dto = data?.info?.firstOrNull()
    return PointBalanceEntity(
        customerGender = dto?.customerGender.orEmpty(),
        pointBalance = dto?.pointBalance.orEmpty(),
        customerBirth = dto?.customerBirth.orEmpty(),
        customerName = dto?.customerName.orEmpty(),
        customerCode = dto?.customerCode.orEmpty(),
        customerPhone = dto?.customerPhone.orEmpty()
    )
}