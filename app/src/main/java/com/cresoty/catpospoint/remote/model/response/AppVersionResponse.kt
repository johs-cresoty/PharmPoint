package com.cresoty.catpospoint.remote.model.response

import com.google.gson.annotations.SerializedName

data class AppVersionResponse(
    @SerializedName("forceUpdate")    val forceUpdate: Boolean,
    @SerializedName("installUrl")     val installUrl: String,
    @SerializedName("messageTitle")   val messageTitle: String?,
    @SerializedName("message")        val message: String?,
)
