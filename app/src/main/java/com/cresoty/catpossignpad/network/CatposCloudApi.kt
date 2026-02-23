package com.cresoty.catpossignpad.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface CatposCloudApi {

    @Headers("Content-Type: application/json")
    @POST("api/point/estimate")
    suspend fun requestExpectSaveAmountComplex(
        @Body request : String
    ) : Response<CatposSaveAmountResponse>

    @Headers("Content-Type: application/json")
    @POST("api/point/estimate")
    suspend fun requestExpectSaveAmount(
        @Body request : String
    ) : Response<CatposSaveAmountResponse>

    @Headers("Content-Type: application/json")
    @POST("api/terminals/customers/code")
    suspend fun requestPointDeltaComplex(
        @Body request : String
    ) : Response<CatposPointDeltaResponse>

    @Headers("Content-Type: application/json")
    @POST("api/terminals/customers/code")
    suspend fun requestPointDelta(
        @Body request : String
    ) : Response<CatposPointDeltaResponse>

    @GET("api/terminals/customers/code")
    suspend fun requestCheckPointBalance(
        @Query("TAXNO") taxno : String,
        @Query("CST_HP") cst_hp : String
    ) : Response<CatposBalanceResponse>

    @GET("api/point/settings")
    suspend fun requestPointSaveSetting(
        @Query("TAXNO") taxno: String,
    ) : Response<CatposSaveSettingResponse>

    @GET("api/point/payment-settings")
    suspend fun requestPointAmountSetting(
        @Query("TAXNO") taxno: String
    ) : Response<CatposAmountSettingResponse>

}