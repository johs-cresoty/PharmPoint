package com.cresoty.catpospoint.remote.api

import com.cresoty.catpospoint.remote.model.request.EstimatePointRequest
import com.cresoty.catpospoint.remote.model.request.UpsertCustomerPointRequest
import com.cresoty.catpospoint.remote.model.response.CustomerResponse
import com.cresoty.catpospoint.remote.model.response.EstimatePointResponse
import com.cresoty.catpospoint.remote.model.response.PointAmountSettingResponse
import com.cresoty.catpospoint.remote.model.response.PointBalanceResponse
import com.cresoty.catpospoint.remote.model.response.PointSaveSettingResponse
import com.cresoty.catpospoint.remote.model.response.UpsertCustomerPointResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CatposCloudApi {

    /**
     * 포인트 적립 설정 정보 조회
     */
    @GET("api/point/settings")
    suspend fun getPointSaveSetting(
        @Query("TAXNO") taxNo: String,
        @Query("CMPTR_NAME") computerName: String,
        @Query("POS_VER") posVersion: String,
        @Query("POS_GUBN") posGubn: String
    ): PointSaveSettingResponse

    /**
     * 결제 금액별 포인트 설정 목록 조회
     */
    @GET("api/point/payment-settings")
    suspend fun getPointAmountSetting(
        @Query("TAXNO") taxNo: String,
        @Query("CMPTR_NAME") computerName: String,
        @Query("POS_VER") posVersion: String,
        @Query("POS_GUBN") posGubn: String
    ): PointAmountSettingResponse

    /**
     * 고객 목록 조회
     */
    @GET("/api/terminals/customers")
    suspend fun getCustomer(
        @Query("CMPTR_NAME") computerName: String,
        @Query("POS_VER") posVersion: String,
        @Query("TAXNO") taxNo: String,
        @Query("CST_HP") customerHp: String
    ): CustomerResponse

    /**
     * 고객 코드 조회
     */
    @GET("api/terminals/customers/code")
    suspend fun getPointBalance(
        @Query("TAXNO") taxNo: String,
        @Query("CST_HP") customerPhone: String,
        @Query("CMPTR_NAME") computerName: String,
        @Query("POS_VER") posVersion: String,
        @Query("POS_GUBN") posGubn: String
    ): PointBalanceResponse

    /**
     *  적립 예상 포인트 조회
     */
    @POST("api/point/estimate")
    suspend fun estimatePoint(
        @Body request: EstimatePointRequest
    ): EstimatePointResponse


    /**
     * 고객 판매정보 갱신 및 고객코드 조회
     */
    @POST("/api/terminals/customers/code")
    suspend fun upsertCustomerPoint(
        @Body request: UpsertCustomerPointRequest
    ): UpsertCustomerPointResponse

}