package com.cresoty.catpospoint.model.event

import android.net.Uri

sealed interface AppEvent {
    data class InstallApk(val uri: Uri) : AppEvent
}
