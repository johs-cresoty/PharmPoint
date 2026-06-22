package com.cresoty.catpospoint.domain.model

/**
 * 포인트 적립 결과 — UpsertCustomerPointUseCase 가 반환하는 도메인 모델.
 *
 * @property pointBalance 적립 후 보유 포인트
 * @property customerName 고객 이름. 신규 고객이거나 응답에서 비어있는 경우 null
 */
data class PointEarnResult(
    val pointBalance: Int,
    val customerName: String?,
)
