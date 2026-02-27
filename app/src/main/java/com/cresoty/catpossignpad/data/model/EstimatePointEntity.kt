package com.cresoty.catpossignpad.data.model

import com.cresoty.catpossignpad.data.mapper.DataMapper
import com.cresoty.catpossignpad.domain.model.EstimatePointResult

data class EstimatePointEntity(
    val sleSeq: String,
    val pointAmount: String
) : DataMapper<EstimatePointResult> {
    override fun toDomain() = EstimatePointResult(
        sleSeq      = sleSeq,
        pointAmount = pointAmount
    )
}
