package com.cresoty.catpospoint.data.remote

import com.cresoty.catpospoint.data.model.CustomerPointDeltaResponseEntity
import com.cresoty.catpospoint.data.model.CustomersEntity
import com.cresoty.catpospoint.data.model.EstimatePointEntity
import com.cresoty.catpospoint.data.model.PointAmountSettingEntity
import com.cresoty.catpospoint.data.model.PointBalanceEntity
import com.cresoty.catpospoint.data.model.PointSaveSettingEntity
import com.cresoty.catpospoint.remote.model.request.EstimatePointRequest
import com.cresoty.catpospoint.remote.model.request.UpsertCustomerPointRequest
import kotlinx.coroutines.flow.Flow

interface PointRemoteDataSource {
    fun getCustomer(
        computerName: String,
        posVersion: String,
        taxNo: String,
        customerHp: String
    ): Flow<CustomersEntity>

    fun upsertCustomerPoint(
        request: UpsertCustomerPointRequest
    ): Flow<CustomerPointDeltaResponseEntity>

    fun estimatePoint(request: EstimatePointRequest): Flow<EstimatePointEntity>

    fun getPointSaveSetting(taxNo: String): Flow<PointSaveSettingEntity>

    fun getPointAmountSetting(taxNo: String): Flow<PointAmountSettingEntity>

    fun getPointBalance(taxNo: String, customerPhone: String): Flow<PointBalanceEntity>
}