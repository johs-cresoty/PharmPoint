package com.cresoty.catpospoint.remote.model.response

import com.google.gson.annotations.SerializedName

data class AppVersionResponse(
    @SerializedName("appType")          val appType: String,
    @SerializedName("installUrl")       val installUrl: String,
    @SerializedName("latestVersion")    val latestVersion: String,
    @SerializedName("minVersion")       val minVersion: String,
    @SerializedName("updateMessage")    val updateMessage: String?,
    @SerializedName("forceUpdateMessage") val forceUpdateMessage: String?,
    @SerializedName("id")               val id: Int,
    @SerializedName("updatedAt")        val updatedAt: String
)
