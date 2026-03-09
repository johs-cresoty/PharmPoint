package com.cresoty.catpospoint.remote.model.response

import com.cresoty.catpospoint.data.model.EstimatePointEntity
import com.google.gson.annotations.SerializedName

data class EstimatePointResponse(
    @SerializedName("MSG")  val message: String?,
    @SerializedName("CODE") val code: String?,
    @SerializedName("DATA") val data: EstimatePointData?,
    @SerializedName("DTL")  val detail: String?
)

data class EstimatePointData(
    @SerializedName("INFO") val info: List<EstimatePointDto>?
)

data class EstimatePointDto(
    @SerializedName("SLE_SEQ")     val sleSeq: String?,
    @SerializedName("GRD_CODE")    val gradeCode: String?,
    @SerializedName("PNT_AMT")     val pointAmount: String?,
    @SerializedName("PAY_PNT_AMT") val payPointAmount: String?,
    @SerializedName("PNT_BLC")     val pointBalance: String?,
    @SerializedName("CST_CODE")    val customerCode: String?
)

fun EstimatePointResponse.toData(): EstimatePointEntity =
    EstimatePointEntity(
        sleSeq      = data?.info?.firstOrNull()?.sleSeq.orEmpty(),
        pointAmount = data?.info?.firstOrNull()?.pointAmount.orEmpty()
    )
