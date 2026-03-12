package com.cresoty.catpospoint.model.state

import android.net.Uri

data class PreviewState(
    var isHideDialog: Boolean = false,
    var subTitle: String? = null,
    var theme: Int? = 0,
    var customImageUri: Uri? = null
)