package com.cresoty.catpospoint.model.interfaces

sealed interface Dialogs {
    data object None : Dialogs
    data object Setting : Dialogs
    data object InputPassword : Dialogs
    data object UpdateRequired : Dialogs  // 다운로드 진행 중 (비-dismissable)
    data object UpdateBlocked : Dialogs   // 업데이트 전까지 차단 (비-dismissable)
}