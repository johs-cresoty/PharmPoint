package com.cresoty.catpospoint.model.enums

import androidx.compose.ui.Alignment


enum class MainThemes(val title : String, val cropAlignment : Alignment) {
    Theme_A("테마A(기본)", Alignment.Center),
    Theme_B("테마B", Alignment.Center),
    Theme_C("테마C", Alignment.BottomCenter),
    Theme_D("테마D", Alignment.BottomCenter),
    Theme_E("테마E", Alignment.TopCenter),
    Theme_CUSTOM("사용자 지정", Alignment.Center)
}