package com.cresoty.catpospoint.remote.api

import com.cresoty.catpospoint.remote.model.request.LoginRequest
import com.cresoty.catpospoint.remote.model.request.LogoutRequest
import com.cresoty.catpospoint.remote.model.request.RefreshTokenRequest
import com.cresoty.catpospoint.remote.model.response.TokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

/** 인증 헤더 불필요한 인증 전용 API (login / refresh / logout) */
interface AppSupportAuthApi {

    @POST("api/v1/app-support/auth/token")
    suspend fun login(@Body request: LoginRequest): TokenResponse

    @POST("api/v1/app-support/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): TokenResponse

    @POST("api/v1/app-support/auth/logout")
    suspend fun logout(@Body request: LogoutRequest)
}
