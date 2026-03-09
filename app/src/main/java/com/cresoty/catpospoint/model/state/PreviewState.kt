package com.cresoty.catpospoint.model.state

data class PreviewState (
    var isHideDialog : Boolean = false,
    var subTitle : String? = null,
    var theme : Int? = 0
)