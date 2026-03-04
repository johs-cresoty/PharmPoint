package com.cresoty.catpossignpad.data.model

import com.cresoty.catpossignpad.data.mapper.DataMapper
import com.cresoty.catpossignpad.domain.model.PointSaveSettingResult

data class PointSaveSettingEntity(
    val isSave: Boolean
) : DataMapper<PointSaveSettingResult> {
    override fun toDomain() = PointSaveSettingResult(isSave = isSave)
}
