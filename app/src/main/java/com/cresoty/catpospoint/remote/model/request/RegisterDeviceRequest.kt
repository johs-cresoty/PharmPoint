package com.cresoty.catpospoint.remote.model.request

import com.google.gson.annotations.SerializedName

data class RegisterDeviceRequest(
    @SerializedName("androidId") val androidId: String,
    @SerializedName("businessRegistrationNumber") val businessRegistrationNumber: Long,
    @SerializedName("currentVersionCode") val currentVersionCode: Int,
    @SerializedName("fcmToken") val fcmToken: String,
    @SerializedName("ip") val ip: String,
    @SerializedName("platform") val platform: String,
)
