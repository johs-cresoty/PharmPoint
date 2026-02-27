package com.cresoty.catpossignpad.remote.model.request

import com.google.gson.annotations.SerializedName


data class UpsertCustomerPointRequest(
    @SerializedName("TAXNO")
    val taxNo: String,
    @SerializedName("CMPTR_NAME")
    val computerName: String,
    @SerializedName("POS_VER")
    val posVersion: String,
    @SerializedName("CST_HP")
    val customerPhone: String,
    @SerializedName("TRN_DATE")
    val transactionDate: String,
    @SerializedName("SLE_SEQ")
    val sleSeq: String? = null,
    @SerializedName("TRN_GUBN")
    val transactionGubn: String? = null,
    @SerializedName("TRN_TIME")
    val transactionTime: String? = null,
    @SerializedName("APP_NUM")
    val approvalNumber: String? = null,
    @SerializedName("TRN_AMT")
    val transactionAmount: String? = null,
    @SerializedName("ADD")
    val payments: List<PaymentDetailRequest>? = null
)

data class PaymentDetailRequest(
    @SerializedName("TRN_GUBN")
    val transactionGubn: String,
    @SerializedName("TRN_DATE")
    val transactionDate: String,
    @SerializedName("TRN_TIME")
    val transactionTime: String,
    @SerializedName("APP_NUM")
    val approvalNumber: String,
    @SerializedName("TRN_AMT")
    val transactionAmount: String
)