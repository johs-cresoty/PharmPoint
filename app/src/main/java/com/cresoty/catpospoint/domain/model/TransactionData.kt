package com.cresoty.catpospoint.domain.model

import com.cresoty.catpospoint.domain.model.command.PaymentDetailCommand

/**
 * 소켓 전문에서 파싱된 거래 데이터.
 * TERMINAL/CAT 포맷 차이를 [TransactionDataParser] 에서 흡수하고,
 * 이후 레이어는 이 모델만 사용한다.
 *
 * @param payments 복합결제일 때만 non-null (EstimatePointCommand.Complex 빌드용)
 */
data class TransactionData(
    val trnDate: String,
    val trnTime: String,
    val appNum: String,
    val trnGubn: String,
    val payAmount: Int,
    val payments: Pair<PaymentDetailCommand, PaymentDetailCommand>? = null,
)
