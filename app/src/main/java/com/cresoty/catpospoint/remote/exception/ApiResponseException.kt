package com.cresoty.catpospoint.remote.exception

class ApiResponseException(
    val api: String,
    val httpStatus: Int? = null,
    val responseCode: String? = null,
    val responseMessage: String? = null,
    val phoneSuffix: String? = null,
    val reason: String,
) : RuntimeException(
    "API[$api] failed: $reason " +
        "(http=${httpStatus ?: "-"}, code=${responseCode ?: "-"}, msg=${responseMessage ?: "-"})"
)
