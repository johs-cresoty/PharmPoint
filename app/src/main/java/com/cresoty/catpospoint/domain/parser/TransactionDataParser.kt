package com.cresoty.catpospoint.domain.parser

import com.cresoty.catpospoint.domain.model.TransactionData
import com.cresoty.catpospoint.domain.model.command.PaymentDetailCommand

/**
 * 소켓 전문 raw 필드(List<String>)를 [TransactionData]로 변환하는 파서.
 *
 * TERMINAL 전문 포맷:
 *   list[1]  = dateTime (14자리: yyyyMMddHHmmss)
 *   list[2]  = appNum (승인번호)
 *   list[3]  = method (거래구분, "P" → "M" 으로 정규화)
 *   list[5]  = otc1 (공급가), list[6] = vat1 (부가세)
 *   list[7]  = appNum2 (복합결제), list[8] = method2
 *   list[10] = otc2, list[11] = vat2 (복합결제)
 *
 * CAT 전문 포맷:
 *   fields[0] = dateTime (14자리)
 *   fields[1] = appNum,  fields[2] = method, fields[3] = amount1
 *   fields[4] = appNum2, fields[5] = method2, fields[6] = amount2 (복합결제)
 */
object TransactionDataParser {

    /** TERMINAL 단일결제 */
    fun parseTerminalSingle(data: List<String>): TransactionData {
        val dateTime = data.getOrElse(1) { "" }
        val otc = data.getOrElse(5) { "0" }.toIntOrNull() ?: 0
        val vat = data.getOrElse(6) { "0" }.toIntOrNull() ?: 0
        return TransactionData(
            trnDate  = dateTime.take(8),
            trnTime  = dateTime.drop(8),
            appNum   = data.getOrElse(2) { "" },
            trnGubn  = data.getOrElse(3) { "M" }.normalizeGubn(),
            payAmount = otc + vat,
        )
    }

    /** TERMINAL 복합결제 */
    fun parseTerminalComplex(data: List<String>): TransactionData {
        val dateTime = data.getOrElse(1) { "" }
        val date = dateTime.take(8)
        val time = dateTime.drop(8)
        val otc1 = data.getOrElse(5)  { "0" }.toIntOrNull() ?: 0
        val vat1 = data.getOrElse(6)  { "0" }.toIntOrNull() ?: 0
        val otc2 = data.getOrElse(10) { "0" }.toIntOrNull() ?: 0
        val vat2 = data.getOrElse(11) { "0" }.toIntOrNull() ?: 0
        val first = PaymentDetailCommand(
            approvalNumber    = data.getOrElse(2) { "" },
            transactionGubn   = data.getOrElse(3) { "M" }.normalizeGubn(),
            transactionDate   = date,
            transactionTime   = time,
            transactionAmount = (otc1 + vat1).toString(),
        )
        val second = PaymentDetailCommand(
            approvalNumber    = data.getOrElse(7) { "" },
            transactionGubn   = data.getOrElse(8) { "M" }.normalizeGubn(),
            transactionDate   = date,
            transactionTime   = time,
            transactionAmount = (otc2 + vat2).toString(),
        )
        return TransactionData(
            trnDate   = date,
            trnTime   = time,
            appNum    = first.approvalNumber,
            trnGubn   = first.transactionGubn,
            payAmount = otc1 + vat1 + otc2 + vat2,
            payments  = first to second,
        )
    }

    /** TERMINAL 포인트 사용 요청 (003) — payAmount 만 유효, 나머지 필드는 미사용 */
    fun parseTerminalUsePoint(data: List<String>): TransactionData {
        val otc = data.getOrElse(3) { "0" }.toIntOrNull() ?: 0
        val vat = data.getOrElse(4) { "0" }.toIntOrNull() ?: 0
        return TransactionData(
            trnDate   = "",
            trnTime   = "",
            appNum    = "",
            trnGubn   = "",
            payAmount = otc + vat,
        )
    }

    /** CAT 포인트 사용 요청 (006) — fields[0] = 거래일자, fields[1] = 결제금액 */
    fun parseCatUsePoint(fields: List<String>): TransactionData {
        val dateTime = fields.getOrElse(0) { "" }
        val amount = fields.getOrElse(1) { "0" }.toIntOrNull() ?: 0
        return TransactionData(
            trnDate   = dateTime.take(8),
            trnTime   = dateTime.drop(8),
            appNum    = "",
            trnGubn   = "",
            payAmount = amount,
        )
    }

    /** CAT 단일결제 */
    fun parseCatSingle(fields: List<String>): TransactionData {
        val dateTime = fields.getOrElse(0) { "" }
        val amount = fields.getOrElse(3) { "0" }.toIntOrNull() ?: 0
        return TransactionData(
            trnDate   = dateTime.take(8),
            trnTime   = dateTime.drop(8),
            appNum    = fields.getOrElse(1) { "" },
            trnGubn   = fields.getOrElse(2) { "M" }.normalizeGubn(),
            payAmount = amount,
        )
    }

    /** CAT 복합결제 */
    fun parseCatComplex(fields: List<String>): TransactionData {
        val dateTime = fields.getOrElse(0) { "" }
        val date = dateTime.take(8)
        val time = dateTime.drop(8)
        val amount1 = fields.getOrElse(3) { "0" }.toIntOrNull() ?: 0
        val amount2 = fields.getOrElse(6) { "0" }.toIntOrNull() ?: 0
        val first = PaymentDetailCommand(
            approvalNumber    = fields.getOrElse(1) { "" },
            transactionGubn   = fields.getOrElse(2) { "M" }.normalizeGubn(),
            transactionDate   = date,
            transactionTime   = time,
            transactionAmount = amount1.toString(),
        )
        val second = PaymentDetailCommand(
            approvalNumber    = fields.getOrElse(4) { "" },
            transactionGubn   = fields.getOrElse(5) { "M" }.normalizeGubn(),
            transactionDate   = date,
            transactionTime   = time,
            transactionAmount = amount2.toString(),
        )
        return TransactionData(
            trnDate   = date,
            trnTime   = time,
            appNum    = first.approvalNumber,
            trnGubn   = first.transactionGubn,
            payAmount = amount1 + amount2,
            payments  = first to second,
        )
    }

    private fun String.normalizeGubn() = if (this == "P") "M" else this
}
