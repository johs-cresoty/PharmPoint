package com.cresoty.catpospoint.model.state

data class CustomerState (
    var phoneNumber : String = "",
    var verifyNumber : String = "",
    var isCustomerExist : Boolean = false,
    val isExistChecking: Boolean = false,
    var verifyResult : Boolean? = null
)
