package com.cresoty.catpospoint.model.enums

enum class PointDeltaProcess(val isClickable: Boolean)
{
    //대기
    NONE(false),

    //적립
    POINT_SAVE_PHONE_NUM(false),
    POINT_SAVE_PROC_DONE(true),

    //사용
    POINT_USE_PHONE_NUM(false),
    POINT_USE_VERIFY_NUM(false),
    POINT_USE_AMOUNT_INPUT(false),
    POINT_USE_PROC_DONE(true),
    POINT_USE_PROC_SHORTAGE_FAIL(true),

    CUSTOMER_PHONE_LOOKUP(false),   // 고객 번호 조회 중
}