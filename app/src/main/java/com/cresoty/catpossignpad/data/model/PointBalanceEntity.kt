package com.cresoty.catpossignpad.data.model

import com.cresoty.catpossignpad.data.mapper.DataMapper
import com.cresoty.catpossignpad.domain.model.PointBalanceResult

data class PointBalanceEntity(
    val balance: String
) : DataMapper<PointBalanceResult> {
    override fun toDomain() = PointBalanceResult(balance = balance)
}
