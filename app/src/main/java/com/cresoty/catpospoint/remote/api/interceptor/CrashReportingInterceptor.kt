package com.cresoty.catpospoint.remote.api.interceptor

import com.cresoty.catpospoint.remote.exception.ApiResponseException
import com.cresoty.catpospoint.util.CrashlyticsLogger
import com.cresoty.catpospoint.util.PiiMask
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.io.IOException

class CrashReportingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val apiPath = request.url.encodedPath
        val method = request.method

        val requestBodyText = readRequestBody(request)

        val response = try {
            chain.proceed(request)
        } catch (e: IOException) {
            CrashlyticsLogger.recordApiError(
                ApiResponseException(
                    api = apiPath,
                    httpMethod = method,
                    path = apiPath,
                    requestBody = requestBodyText,
                    reason = "network_error: ${e.javaClass.simpleName} ${e.message ?: ""}",
                )
            )
            throw e
        }

        if (!response.isSuccessful) {
            val responseBodyText = readResponseBody(response)
            CrashlyticsLogger.recordApiError(
                ApiResponseException(
                    api = apiPath,
                    httpMethod = method,
                    path = apiPath,
                    httpStatus = response.code,
                    requestBody = requestBodyText,
                    responseBody = responseBodyText,
                    reason = "http_${response.code}",
                )
            )
        }

        return response
    }

    private fun readRequestBody(request: okhttp3.Request): String {
        val body = request.body ?: return ""
        return try {
            val buffer = Buffer()
            body.writeTo(buffer)
            PiiMask.maskSensitiveJsonFields(buffer.readUtf8()).take(MAX_BODY_LEN)
        } catch (e: Exception) {
            "(read_failed: ${e.message})"
        }
    }

    private fun readResponseBody(response: Response): String {
        return try {
            val peek = response.peekBody(MAX_PEEK_BYTES)
            PiiMask.maskSensitiveJsonFields(peek.string()).take(MAX_BODY_LEN)
        } catch (e: Exception) {
            "(read_failed: ${e.message})"
        }
    }

    companion object {
        private const val MAX_BODY_LEN = 800
        private const val MAX_PEEK_BYTES = 4096L
    }
}
