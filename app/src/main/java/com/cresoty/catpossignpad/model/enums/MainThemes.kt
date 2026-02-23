package com.cresoty.catpossignpad.model.enums

import androidx.compose.ui.Alignment


enum class MainThemes(val title : String, val cropAlignment : Alignment) {
    Theme_A("테마A", Alignment.BottomCenter),
    Theme_B("테마B", Alignment.BottomCenter),
    Theme_C("테마C", Alignment.TopCenter),
    Theme_D("테마D", Alignment.Center),
    Theme_E("테마E", Alignment.Center)
}