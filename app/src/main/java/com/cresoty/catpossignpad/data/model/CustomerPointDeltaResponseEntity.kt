package com.cresoty.catpossignpad.data.model

import com.cresoty.catpossignpad.data.mapper.DataMapper
import com.cresoty.catpossignpad.domain.model.CustomerPointDelta
import com.cresoty.catpossignpad.domain.model.CustomerPointDeltaInfo
import com.cresoty.catpossignpad.domain.model.CustomerPointDeltaResult


data class CustomerPointDeltaResponseEntity(
    val message: String,
    val code: String,
    val data: CustomerPointDeltaEntity?,
    val detail: String?
) : DataMapper<CustomerPointDeltaResult> {
    override fun toDomain() = CustomerPointDeltaResult(
        message = message,
        code = code,
        data = data?.toDomain(),
        detail = detail
    )

}

data class CustomerPointDeltaEntity(
    val info: List<CustomerPointDeltaInfoEntity>
) : DataMapper<CustomerPointDelta> {
    override fun toDomain() = CustomerPointDelta(
        info = info.map { it.toDomain() }
    )

}

data class CustomerPointDeltaInfoEntity(
    val sleSeq: String,
    val customerCode: String,
    val customerPhone: String,
    val customerName: String,
    val pointAmount: String,
    val pointBalance: String
) : DataMapper<CustomerPointDeltaInfo> {
    override fun toDomain() = CustomerPointDeltaInfo(
        sleSeq = sleSeq,
        customerCode = customerCode,
        customerPhone = customerPhone,
        customerName = customerName,
        pointAmount = pointAmount,
        pointBalance = pointBalance
    )

}