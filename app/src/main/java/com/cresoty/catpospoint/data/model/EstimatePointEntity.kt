package com.cresoty.catpospoint.data.model

import com.cresoty.catpospoint.data.mapper.DataMapper
import com.cresoty.catpospoint.domain.model.EstimatePointResult

data class EstimatePointEntity(
    val sleSeq: String,
    val pointAmount: String
) : DataMapper<EstimatePointResult> {
    override fun toDomain() = EstimatePointResult(
        sleSeq      = sleSeq,
        pointAmount = pointAmount
    )
}
