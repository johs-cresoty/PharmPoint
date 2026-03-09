package com.cresoty.catpospoint.data.model

import com.cresoty.catpospoint.data.mapper.DataMapper
import com.cresoty.catpospoint.domain.model.PointSaveSettingResult

data class PointSaveSettingEntity(
    val isSave: Boolean
) : DataMapper<PointSaveSettingResult> {
    override fun toDomain() = PointSaveSettingResult(isSave = isSave)
}
