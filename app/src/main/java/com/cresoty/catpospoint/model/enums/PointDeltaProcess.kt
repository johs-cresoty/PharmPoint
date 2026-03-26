package com.cresoty.catpospoint.model.enums

import com.cresoty.catpospoint.presentation.result.ResultContract

enum class PointUseSource { CAT, CAT_WITH_CUSTOMER, TERMINAL, MANUAL }
enum class PaymentType {
    SINGLE,
    MULTIPLE
}

sealed class PointDeltaProcess(val isClickable: Boolean)
{
    //대기
    data object NONE : PointDeltaProcess(false)

    //적립
    data object POINT_SAVE_PHONE_NUM : PointDeltaProcess(false)
    data object POINT_SAVE_PROC_DONE : PointDeltaProcess(true)

    //사용
    data class POINT_USE_PHONE_NUM(val source: PointUseSource) : PointDeltaProcess(false)
    data class POINT_USE_VERIFY_NUM(val source: PointUseSource) : PointDeltaProcess(false)
    data class POINT_USE_AMOUNT_INPUT(val source: PointUseSource, val withCustomer: Boolean = false) : PointDeltaProcess(false)
    data object POINT_USE_PROC_DONE : PointDeltaProcess(true)
    data object POINT_USE_PROC_SHORTAGE_FAIL : PointDeltaProcess(true)

    //잔액 조회 결과
    data class POINT_BALANCE_RESULT(val resultState: ResultContract.State) : PointDeltaProcess(true)

    data object REQUEST_CST : PointDeltaProcess(false)   // 휴대폰 번호 + 고객 번호 요청
    data object REQUEST_NUM : PointDeltaProcess(false)   //휴대폰 번호 요청
}
