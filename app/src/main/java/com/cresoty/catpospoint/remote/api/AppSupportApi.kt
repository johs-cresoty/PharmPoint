package com.cresoty.catpospoint.remote.api

import com.cresoty.catpospoint.remote.model.request.AppVersionCheckRequest
import com.cresoty.catpospoint.remote.model.request.RegisterDeviceRequest
import com.cresoty.catpospoint.remote.model.response.AppVersionResponse
import retrofit2.http.Body
import retrofit2.http.POST

/** Bearer 토큰 인증이 필요한 app-support API (AuthInterceptor 가 헤더 자동 주입) */
interface AppSupportApi {

    @POST("api/v1/app-support/message")
    suspend fun registerDevice(@Body request: RegisterDeviceRequest)

    @POST("api/v1/app-support/version/check")
    suspend fun checkAppVersion(@Body request: AppVersionCheckRequest): AppVersionResponse
}
