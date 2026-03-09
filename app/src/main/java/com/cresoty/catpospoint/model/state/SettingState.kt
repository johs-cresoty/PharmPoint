package com.cresoty.catpospoint.model.state

import com.cresoty.catpospoint.model.interfaces.Dialogs

data class SettingState(
    var dialog : Dialogs = Dialogs.None,
    var selectedIndex : Int = 0,
    var isPasswordCorrect : Boolean = true,
    var password : String = "",
)
