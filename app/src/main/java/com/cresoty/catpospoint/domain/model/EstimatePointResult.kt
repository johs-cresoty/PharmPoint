package com.cresoty.catpospoint.domain.model

data class EstimatePointResult(
    val sleSeq: String,
    val pointAmount: String
)
// null = 재시도 소진 후 graceful advance (DataResource.Success(null))
