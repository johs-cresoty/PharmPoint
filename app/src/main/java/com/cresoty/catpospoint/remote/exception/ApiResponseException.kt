package com.cresoty.catpospoint.remote.exception

class ApiResponseException(
    val api: String,
    val httpMethod: String? = null,
    val httpStatus: Int? = null,
    val responseCode: String? = null,
    val responseMessage: String? = null,
    val requestBody: String? = null,
    val responseBody: String? = null,
    val phoneSuffix: String? = null,
    val reason: String,
) : RuntimeException(
    "API[${httpMethod ?: ""} $api] failed: $reason " +
        "(http=${httpStatus ?: "-"}, code=${responseCode ?: "-"}, msg=${responseMessage ?: "-"})"
)
