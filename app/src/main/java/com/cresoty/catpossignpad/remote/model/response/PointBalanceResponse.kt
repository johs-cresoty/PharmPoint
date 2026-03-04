package com.cresoty.catpossignpad.remote.model.response

import com.cresoty.catpossignpad.data.model.PointBalanceEntity
import com.google.gson.annotations.SerializedName

data class PointBalanceResponse(
    @SerializedName("MSG")  val message: String?,
    @SerializedName("CODE") val code: String?,
    @SerializedName("DATA") val data: PointBalanceData?,
    @SerializedName("DTL")  val detail: String?
)

data class PointBalanceData(
    @SerializedName("INFO") val info: List<PointBalanceDto>?
)

data class PointBalanceDto(
    @SerializedName("PNT_BLC") val pointBalance: String?
)

fun PointBalanceResponse.toData(): PointBalanceEntity =
    PointBalanceEntity(
        balance = data?.info?.firstOrNull()?.pointBalance ?: "0"
    )
