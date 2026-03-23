package com.cresoty.catpospoint.remote.model.request

import com.google.gson.annotations.SerializedName

data class AppVersionCheckRequest(
    @SerializedName("currentVersionCode") val currentVersionCode: Int,
    @SerializedName("platform")           val platform: String
)
