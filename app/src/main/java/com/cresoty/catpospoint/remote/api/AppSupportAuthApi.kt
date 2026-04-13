package com.cresoty.catpospoint.remote.api

import com.cresoty.catpospoint.remote.model.request.LoginRequest
import com.cresoty.catpospoint.remote.model.request.LogoutRequest
import com.cresoty.catpospoint.remote.model.request.RefreshTokenRequest
import com.cresoty.catpospoint.remote.model.response.TokenResponse
import com.cresoty.catpospoint.remote.model.response.ValidatePharmacyResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/** 인증 헤더 불필요한 인증 전용 API (login / refresh / logout / validate-pharmacy) */
interface AppSupportAuthApi {

    @POST("api/v1/pharmpoint/auth/token")
    suspend fun login(@Body request: LoginRequest): TokenResponse

    @POST("api/v1/pharmpoint/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): TokenResponse

    @POST("api/v1/pharmpoint/auth/logout")
    suspend fun logout(@Body request: LogoutRequest)

    @GET("api/v1/pharmpoint/auth/validate-pharmacy")
    suspend fun validatePharmacy(
        @Query("businessRegistrationNumber") businessRegistrationNumber: String,
    ): ValidatePharmacyResponse
}
