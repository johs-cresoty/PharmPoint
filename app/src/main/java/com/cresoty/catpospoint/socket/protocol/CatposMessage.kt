package com.cresoty.catpospoint.socket.protocol

data class CatposMessage(
    val system: String,
    val command: String,
    val fields: List<String>
)