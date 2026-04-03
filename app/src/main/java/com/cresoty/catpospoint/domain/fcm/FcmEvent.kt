package com.cresoty.catpospoint.domain.fcm

sealed class FcmEvent {
    data class TokenRefreshed(val token: String) : FcmEvent()
    data class MessageReceived(
        val title: String,
        val body: String,
        val data: Map<String, String>,
    ) : FcmEvent()
}
