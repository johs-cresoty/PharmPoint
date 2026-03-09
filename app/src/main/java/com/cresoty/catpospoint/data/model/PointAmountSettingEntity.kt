package com.cresoty.catpospoint.data.model

import com.cresoty.catpospoint.data.mapper.DataMapper
import com.cresoty.catpospoint.domain.model.PointAmountSettingResult

data class PointAmountSettingEntity(
    val minAmount: Int
) : DataMapper<PointAmountSettingResult> {
    override fun toDomain() = PointAmountSettingResult(minAmount = minAmount)
}
