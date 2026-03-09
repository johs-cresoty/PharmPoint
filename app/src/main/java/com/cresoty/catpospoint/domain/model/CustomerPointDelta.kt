package com.cresoty.catpospoint.domain.model


data class CustomerPointDeltaResult(
    val message: String,
    val code: String,
    val data: CustomerPointDelta?,
    val detail: String?
)

data class CustomerPointDelta(val info: List<CustomerPointDeltaInfo>)

data class CustomerPointDeltaInfo(
    val sleSeq: String,
    val customerCode: String,
    val customerPhone: String,
    val customerName: String,
    val pointAmount: String,
    val pointBalance: String
)