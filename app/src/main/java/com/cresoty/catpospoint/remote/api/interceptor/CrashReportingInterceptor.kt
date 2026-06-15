package com.cresoty.catpospoint.remote.api.interceptor

import com.cresoty.catpospoint.remote.exception.ApiResponseException
import com.cresoty.catpospoint.util.CrashlyticsLogger
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class CrashReportingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val apiPath = request.url.encodedPath

        val response = try {
            chain.proceed(request)
        } catch (e: IOException) {
            CrashlyticsLogger.recordApiError(
                ApiResponseException(
                    api = apiPath,
                    reason = "network_error: ${e.javaClass.simpleName} ${e.message ?: ""}",
                )
            )
            throw e
        }

        if (!response.isSuccessful) {
            CrashlyticsLogger.recordApiError(
                ApiResponseException(
                    api = apiPath,
                    httpStatus = response.code,
                    reason = "http_${response.code}",
                )
            )
        }

        return response
    }
}
