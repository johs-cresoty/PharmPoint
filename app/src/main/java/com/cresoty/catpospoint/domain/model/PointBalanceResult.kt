package com.cresoty.catpospoint.domain.model

data class PointBalanceResult(
    val customerGender: String,   // CST_GNDR
    val pointBalance: String,     // PNT_BLC
    val customerBirth: String,    // CST_BRTH
    val customerName: String,     // CST_NAME
    val customerCode: String,     // CST_CODE
    val customerPhone: String     // CST_HP
)