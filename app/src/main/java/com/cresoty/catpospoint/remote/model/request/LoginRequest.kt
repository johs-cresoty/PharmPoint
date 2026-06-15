package com.cresoty.catpospoint.remote.model.request

data class LoginRequest(
    val id: String,
    val password: String,
    val change: Boolean? = null,
)
