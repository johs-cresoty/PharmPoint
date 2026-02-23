package com.cresoty.catpossignpad.model.state

data class CustomerState (
    var phoneNumber : String = "",
    var verifyNumber : String = "",
    var isCustomerExist : Boolean = true,
    var verifyResult : Boolean? = null
)
