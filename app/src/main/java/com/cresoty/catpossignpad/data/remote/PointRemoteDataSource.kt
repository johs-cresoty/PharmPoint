package com.cresoty.catpossignpad.data.remote

import com.cresoty.catpossignpad.data.model.CustomerPointDeltaResponseEntity
import com.cresoty.catpossignpad.data.model.CustomersEntity
import com.cresoty.catpossignpad.data.model.EstimatePointEntity
import com.cresoty.catpossignpad.data.model.PointAmountSettingEntity
import com.cresoty.catpossignpad.data.model.PointBalanceEntity
import com.cresoty.catpossignpad.data.model.PointSaveSettingEntity
import com.cresoty.catpossignpad.remote.model.request.EstimatePointRequest
import com.cresoty.catpossignpad.remote.model.request.UpsertCustomerPointRequest
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