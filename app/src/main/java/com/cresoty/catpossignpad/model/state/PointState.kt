package com.cresoty.catpossignpad.model.state

data class PointState (
//    var phoneNum : String = "",
    var pointDelta : String = "",               // 포인트 변화량(예상적립포인트, 사용포인트)
    var pointBalance : String = "",             // 포인트 잔액
    var isPersonalInfoUse : Boolean = true,     // 개인정보 사용
    var isMasking : Boolean = true              // 전화번호 마스킹 여부
)

