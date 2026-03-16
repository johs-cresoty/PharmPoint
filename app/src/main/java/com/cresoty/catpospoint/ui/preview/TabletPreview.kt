package com.cresoty.catpospoint.ui.preview


import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    showBackground = true,
    device = "spec:width=800px,height=1340px,dpi=213",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
annotation class TabletPreview(
    val name: String = ""
)