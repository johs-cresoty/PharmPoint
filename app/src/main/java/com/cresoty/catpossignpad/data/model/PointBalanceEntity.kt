package com.cresoty.catpossignpad.data.model

import com.cresoty.catpossignpad.data.mapper.DataMapper
import com.cresoty.catpossignpad.domain.model.PointBalanceResult

data class PointBalanceEntity(
    val customerGender: String,
    val pointBalance: String,
    val customerBirth: String,
    val customerName: String,
    val customerCode: String,
    val customerPhone: String
) : DataMapper<PointBalanceResult> {
    override fun toDomain() = PointBalanceResult(
        customerGender = customerGender,
        pointBalance = pointBalance,
        customerBirth = customerBirth,
        customerName = customerName,
        customerCode = customerCode,
        customerPhone = customerPhone
    )
}