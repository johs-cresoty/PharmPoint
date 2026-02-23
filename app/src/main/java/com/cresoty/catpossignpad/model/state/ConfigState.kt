package com.cresoty.catpossignpad.model.state

data class ConfigState (
    var bizNo : String = "",
    var storeName : String = "",
    var themeIndex : Int = 0,
    var subTitle : String = "",
    var timeout : Int = 5,
    var minPoint : Int = 1000,
    var minAmount : Int = 20000,
    var isIdVerify : Boolean = false,
    var isPointUse : Boolean = true,
    var isSave : Boolean = true
)