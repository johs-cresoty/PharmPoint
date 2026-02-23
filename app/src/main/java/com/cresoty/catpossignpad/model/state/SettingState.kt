package com.cresoty.catpossignpad.model.state

import com.cresoty.catpossignpad.model.interfaces.Dialogs

data class SettingState(
    var dialog : Dialogs = Dialogs.None,
    var selectedIndex : Int = 0,
    var isPasswordCorrect : Boolean = true,
    var password : String = "",
)
