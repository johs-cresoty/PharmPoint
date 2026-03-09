package com.cresoty.catpospoint.data.model

import com.cresoty.catpospoint.data.mapper.DataMapper
import com.cresoty.catpospoint.domain.model.PointBalanceResult

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