package com.cresoty.catpospoint.remote.model.response


import com.cresoty.catpospoint.data.model.CustomerPointDeltaEntity
import com.cresoty.catpospoint.data.model.CustomerPointDeltaInfoEntity
import com.cresoty.catpospoint.data.model.CustomerPointDeltaResponseEntity
import com.google.gson.annotations.SerializedName

data class UpsertCustomerPointResponse(
    @SerializedName("MSG")
    val message: String,

    @SerializedName("CODE")
    val code: String,

    @SerializedName("DATA")
    val data: UpsertCustomerPointData?,

    @SerializedName("DTL")
    val detail: String?
)

data class UpsertCustomerPointData(
    @SerializedName("INFO")
    val info: List<CustomerPointDeltaDto>?
)

data class CustomerPointDeltaDto(
    @SerializedName("SLE_SEQ")
    val sleSeq: String,

    @SerializedName("CST_CODE")
    val customerCode: String,

    @SerializedName("CST_HP")
    val customerPhone: String,

    @SerializedName("CST_NAME")
    val customerName: String,

    @SerializedName("PNT_AMT")
    val pointAmount: String,

    @SerializedName("PNT_BLC")
    val pointBalance: String
)

fun UpsertCustomerPointResponse.toData(): CustomerPointDeltaResponseEntity =
    CustomerPointDeltaResponseEntity(
        message = message,
        code = code,
        data = data?.toData(),
        detail = detail
    )

fun UpsertCustomerPointData.toData(): CustomerPointDeltaEntity =
    CustomerPointDeltaEntity(
        info = info?.map { it.toData() } ?: emptyList()
    )

fun CustomerPointDeltaDto.toData(): CustomerPointDeltaInfoEntity =
    CustomerPointDeltaInfoEntity(
        sleSeq = sleSeq,
        customerCode = customerCode,
        customerPhone = customerPhone,
        customerName = customerName,
        pointAmount = pointAmount,
        pointBalance = pointBalance
    )