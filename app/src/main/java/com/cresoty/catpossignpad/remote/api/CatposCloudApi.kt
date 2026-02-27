package com.cresoty.catpossignpad.remote.api

import com.cresoty.catpossignpad.network.CatposAmountSettingResponse
import com.cresoty.catpossignpad.network.CatposBalanceResponse
import com.cresoty.catpossignpad.network.CatposSaveSettingResponse
import com.cresoty.catpossignpad.remote.model.request.EstimatePointRequest
import com.cresoty.catpossignpad.remote.model.request.UpsertCustomerPointRequest
import com.cresoty.catpossignpad.remote.model.response.CustomerResponse
import com.cresoty.catpossignpad.remote.model.response.EstimatePointResponse
import com.cresoty.catpossignpad.remote.model.response.UpsertCustomerPointResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CatposCloudApi {

    @POST("api/point/estimate")
    suspend fun estimatePoint(
        @Body request: EstimatePointRequest
    ): EstimatePointResponse


    @GET("api/terminals/customers/code")
    suspend fun requestCheckPointBalance(
        @Query("TAXNO") taxno: String,
        @Query("CST_HP") cst_hp: String
    ): Response<CatposBalanceResponse>

    @GET("api/point/settings")
    suspend fun requestPointSaveSetting(
        @Query("TAXNO") taxno: String,
    ): Response<CatposSaveSettingResponse>

    @GET("api/point/payment-settings")
    suspend fun requestPointAmountSetting(
        @Query("TAXNO") taxno: String
    ): Response<CatposAmountSettingResponse>

    @GET("/api/terminals/customers")
    suspend fun getCustomer(
        @Query("CMPTR_NAME") computerName: String,
        @Query("POS_VER") posVersion: String,
        @Query("TAXNO") taxNo: String,
        @Query("CST_HP") customerHp: String
    ): CustomerResponse


    @POST("/api/terminals/customers/code")
    suspend fun upsertCustomerPoint(
        @Body request: UpsertCustomerPointRequest
    ): UpsertCustomerPointResponse

}