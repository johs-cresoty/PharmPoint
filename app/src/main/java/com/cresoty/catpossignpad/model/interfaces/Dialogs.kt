package com.cresoty.catpossignpad.model.interfaces

sealed interface Dialogs {
    data object None : Dialogs
    data object Setting : Dialogs
    data object InputPassword : Dialogs
}