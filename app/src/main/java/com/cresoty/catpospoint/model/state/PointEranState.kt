package com.cresoty.catpospoint.model.state

data class PointEranState (
    var approvalDate : String = "",         // yyyyMMdd
    var transactionMethod : String = "",    // 거래수단
    var approvalNumber : String = ""        // 승인번호
)