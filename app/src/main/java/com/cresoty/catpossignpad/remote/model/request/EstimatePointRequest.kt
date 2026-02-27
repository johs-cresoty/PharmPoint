package com.cresoty.catpossignpad.remote.model.request

import com.google.gson.annotations.SerializedName

data class EstimatePointRequest(
    @SerializedName("TAXNO")      val taxNo: String,
    @SerializedName("CMPTR_NAME") val computerName: String,
    @SerializedName("POS_VER")    val posVersion: String,
    // 단건 전용 (복합 시 null → Gson이 직렬화 제외)
    @SerializedName("TRN_DATE")   val trnDate: String? = null,
    @SerializedName("TRN_GUBN")   val trnGubn: String? = null,
    @SerializedName("TRN_AMT")    val trnAmt: String? = null,
    @SerializedName("APP_NUM")    val appNum: String? = null,
    // 복합 전용
    @SerializedName("ADD")        val payments: List<PaymentDetailRequest>? = null
)
