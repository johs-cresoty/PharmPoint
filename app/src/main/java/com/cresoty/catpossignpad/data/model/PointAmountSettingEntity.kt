package com.cresoty.catpossignpad.data.model

import com.cresoty.catpossignpad.data.mapper.DataMapper
import com.cresoty.catpossignpad.domain.model.PointAmountSettingResult

data class PointAmountSettingEntity(
    val minAmount: Int
) : DataMapper<PointAmountSettingResult> {
    override fun toDomain() = PointAmountSettingResult(minAmount = minAmount)
}
