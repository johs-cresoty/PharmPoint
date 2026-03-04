package com.cresoty.catpossignpad.remote.api

import com.cresoty.catpossignpad.remote.model.request.EstimatePointRequest
import com.cresoty.catpossignpad.remote.model.request.UpsertCustomerPointRequest
import com.cresoty.catpossignpad.remote.model.response.CustomerResponse
import com.cresoty.catpossignpad.remote.model.response.EstimatePointResponse
import com.cresoty.catpossignpad.remote.model.response.PointAmountSettingResponse
import com.cresoty.catpossignpad.remote.model.response.PointBalanceResponse
import com.cresoty.catpossignpad.remote.model.response.PointSaveSettingResponse
import com.cresoty.catpossignpad.remote.model.response.UpsertCustomerPointResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CatposCloudApi {


    @GET("api/point/settings")
    suspend fun getPointSaveSetting(
        @Query("TAXNO") taxNo: String,
        @Query("CMPTR_NAME") computerName: String,
        @Query("POS_VER") posVersion: String,
        @Query("POS_GUBN") posGubn: String
    ): PointSaveSettingResponse

    @GET("api/point/payment-settings")
    suspend fun getPointAmountSetting(
        @Query("TAXNO") taxNo: String,
        @Query("CMPTR_NAME") computerName: String,
        @Query("POS_VER") posVersion: String,
        @Query("POS_GUBN") posGubn: String
    ): PointAmountSettingResponse

    @GET("/api/terminals/customers")
    suspend fun getCustomer(
        @Query("CMPTR_NAME") computerName: String,
        @Query("POS_VER") posVersion: String,
        @Query("TAXNO") taxNo: String,
        @Query("CST_HP") customerHp: String
    ): CustomerResponse

    @GET("api/terminals/customers/code")
    suspend fun getPointBalance(
        @Query("TAXNO") taxNo: String,
        @Query("CST_HP") customerPhone: String,
        @Query("CMPTR_NAME") computerName: String,
        @Query("POS_VER") posVersion: String,
        @Query("POS_GUBN") posGubn: String
    ): PointBalanceResponse

    @POST("api/point/estimate")
    suspend fun estimatePoint(
        @Body request: EstimatePointRequest
    ): EstimatePointResponse


    @POST("/api/terminals/customers/code")
    suspend fun upsertCustomerPoint(
        @Body request: UpsertCustomerPointRequest
    ): UpsertCustomerPointResponse

}