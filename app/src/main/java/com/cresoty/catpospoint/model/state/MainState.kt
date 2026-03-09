package com.cresoty.catpospoint.model.state

import com.cresoty.catpospoint.model.enums.PointDeltaProcess

data class MainState (
//    var cmd : String = "",
//    var theme : MainThemes = MainThemes.Theme_A,
    var pointDeltaStep : PointDeltaProcess = PointDeltaProcess.NONE,
    var paymentAmount : String = ""
)