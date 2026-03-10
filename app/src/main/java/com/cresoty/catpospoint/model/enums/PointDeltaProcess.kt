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

    REQUEST_CST(false),   // 휴대폰 번호 + 고객 번호 요청
    REQUEST_NUM(false),   //휴대폰 번호 요청
}