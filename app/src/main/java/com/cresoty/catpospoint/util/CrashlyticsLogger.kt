package com.cresoty.catpospoint.util

import com.cresoty.catpospoint.remote.exception.ApiResponseException
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics

object CrashlyticsLogger {

    fun setStoreIdentity(taxNo: String, androidId: String, fcmToken: String) {
        val c = Firebase.crashlytics
        if (taxNo.isNotBlank()) c.setUserId(taxNo)
        c.setCustomKey(KEY_TAX_NO, taxNo)
        c.setCustomKey(KEY_ANDROID_ID, androidId)
        c.setCustomKey(KEY_FCM_TOKEN, fcmToken)
    }

    fun recordApiError(ex: ApiResponseException) {
        val c = Firebase.crashlytics
        c.setCustomKey("last_api", ex.api)
        ex.httpMethod?.let       { c.setCustomKey("last_http_method",   it) }
        ex.httpStatus?.let       { c.setCustomKey("last_http_status",   it) }
        ex.responseCode?.let     { c.setCustomKey("last_response_code", it) }
        ex.phoneSuffix?.let      { c.setCustomKey("last_phone_suffix",  it) }
        ex.requestBody?.takeIf { it.isNotBlank() }?.let {
            c.setCustomKey("last_request_body", it.take(MAX_KEY_LEN))
        }
        ex.responseBody?.takeIf { it.isNotBlank() }?.let {
            c.setCustomKey("last_response_body", it.take(MAX_KEY_LEN))
        }
        c.log(
            "API_ERROR ${ex.httpMethod ?: ""} ${ex.api} " +
                "reason=${ex.reason} " +
                "http=${ex.httpStatus ?: "-"} " +
                "code=${ex.responseCode ?: "-"} " +
                "phone=${ex.phoneSuffix ?: "-"} " +
                "req=${ex.requestBody?.take(200) ?: "-"} " +
                "res=${ex.responseBody?.take(200) ?: "-"}"
        )
        c.recordException(ex)
    }

    private const val KEY_TAX_NO = "tax_no"
    private const val KEY_ANDROID_ID = "android_id"
    private const val KEY_FCM_TOKEN = "fcm_token"
    private const val MAX_KEY_LEN = 1024
}
