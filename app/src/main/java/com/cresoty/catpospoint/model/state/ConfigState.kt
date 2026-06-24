package com.cresoty.catpospoint.model.state

data class ConfigState (
    var bizNo : String = "",
    var storeName : String = "",
    var themeIndex : Int = 0,
    var subTitle : String = "",
    var customImageUri : String = "",
    var autoCloseTimeout : Int = 5,
    var inactiveCloseTimeout : Int = 10,
    var minPoint : Int = 1000,
    var minAmount : Int = 20000,
    var isIdVerify : Boolean = false,
    var isMinPointEnabled : Boolean = true,
    var isSave : Boolean = true,
    var brightness : Float = 1.0f,
    var password : String = ""
)